package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	
	/*
	public List<ProductDTO> findAll(){
		
		List<Product> list = repository.findAll();
		System.out.println(list.stream());
		return list.stream()
				.map(x -> new ProductDTO(x)) 
				.collect(Collectors.toList());
	} */
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		Page<Product> list = repository.findAll(pageRequest);
		return list.map(x -> new ProductDTO(x));
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		//Optional<Product> obj = repository.findById(id);
		var obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Objeto não encontrado"));
		return new ProductDTO(entity,entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO item) {
		Product entity = new Product();
		//entity.setName(item.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity);
		
	}
	
	@Transactional
	public ProductDTO update(ProductDTO item,Long id) {
		try {
			Product entity = repository.getOne(id);
			//entity.setName(item.getName());
			entity = repository.save(entity);
			return new ProductDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found "+id);
		}
		
		
	}

	public void delete(Long id) {
		try {
		repository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found "+id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation");
		}
		
	}

	
}