package com.chrisenoch.col.CostOfLiving.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrisenoch.col.CostOfLiving.annotation.ToUpper;
import com.chrisenoch.col.CostOfLiving.entity.COLIndex;
import com.chrisenoch.col.CostOfLiving.entity.COLResults;
import com.chrisenoch.col.CostOfLiving.error.COLIndexNotFoundException;
import com.chrisenoch.col.CostOfLiving.repository.RateRepository;

@Service
public class CostOfLivingServiceImplService implements CostOfLivingService{
	
	@Autowired
	RateRepository repository;
	
	@Override
	public List<COLIndex> findColIndexes(){
		return repository.findAll();
	}
	
	@Override
	public Optional<COLIndex>findByCity(@ToUpper String city) {
		return repository.findByCity(city);
	}
	
	@Override
	public List<COLIndex> findColIndexes(String theDate){
		System.out.println("Inside find by date");
		return repository.findByDate(theDate);
	}
	
	@Override
	public Optional<List<COLIndex>> findColIndexesByCountry(@ToUpper String country){
		System.out.println("Inside findColIndexesByCountry " + country);
		return repository.findByCountry(country);
	}
	
	@Override
	public COLResults calculateEquivalentSalary(BigDecimal amount, @ToUpper String city1,@ToUpper String city2) { //Improve code. See currency eg and null. Need to test for null.
		BigDecimal theBase = repository.findByCity(city1).orElseThrow(()-> new COLIndexNotFoundException(city1)).getRate();
		BigDecimal  theCode = repository.findByCity(city2).orElseThrow(()-> new COLIndexNotFoundException(city2)).getRate();
		
		BigDecimal  total = (theBase.divide(theCode, 2, RoundingMode.HALF_EVEN)).multiply(amount);
		System.out.println("Total: " + total);
		
		return new COLResults(city1, city2, amount, total);
	}
	
	@Override
	public List<COLResults> calculateEquivalentSalaryByCountry(BigDecimal amount, COLIndex colIndex, @ToUpper String country) { //Improve code. See currency eg and null. Need to test for null.
		List<COLIndex> COLIndexes = findColIndexesByCountry(country).orElseThrow(()-> new COLIndexNotFoundException(country));
		System.out.println("Inside find by country " + country + " " + amount + " " + colIndex.getCity());
		COLIndexes.forEach(System.out::println);
		//List<COLResults> results = COLIndexes.stream().mapToDouble(r->r.getRate()).
		List<COLResults> results = COLIndexes.stream().map(
				r -> {
					System.out.println("colIndex.getCity() " + colIndex.getCity() + " " 
							+ "r.getCity() " +  r.getCity()
							+ "colIndex.getRate() " + colIndex.getRate()
							+ "r.getRate() " + r.getRate()
							
							);
					return new COLResults(colIndex.getCity()
				, r.getCity(), amount, (colIndex.getRate().divide(r.getRate(), 2, RoundingMode.HALF_EVEN)).multiply(amount));
				//r.getCity(), amount, colIndex.getRate()/r.getRate() * amount );
				}
				
				).collect(Collectors.toList());
						
		results.forEach(System.out::println);
		return results; 
	}

	@Override
	//To practise implementing custom methods using SpringData
	public List<COLIndex> getRatesByShortCountryName(@ToUpper String country){
		return repository.getRatesByShortCountryName(country);
	}

	@Override
	public Optional<List<COLIndex>> findByCountryStartingWith(@ToUpper String country) {
		return repository.findByCountryStartingWith(country);
	}
		
}


