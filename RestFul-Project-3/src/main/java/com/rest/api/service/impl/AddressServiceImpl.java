package com.rest.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rest.api.Repository.AddressRepository;
import com.rest.api.Repository.UserRepository;
import com.rest.api.entity.AddressEntity;
import com.rest.api.entity.UserEntity;
import com.rest.api.service.AddressService;
import com.rest.api.sharedClass.DTO.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService{
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		
		List<AddressDTO> returnValue = new ArrayList<>();
		
		 UserEntity userEntity = userRepository.findUserByUserId(userId);
		 
		 if(userEntity == null )
			 return returnValue;
		 
		 Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		 
		 for(AddressEntity addressEntity  : addresses) {
			 returnValue.add(new ModelMapper().map(addressEntity, AddressDTO.class));
		 }
		return returnValue;
	}

	@Override
	public AddressDTO getAddress(String addressId) {
		
		AddressDTO returnValue = null;
		
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		
		if(addressEntity !=null )
			returnValue = new ModelMapper().map(addressEntity, AddressDTO.class);
		
		return returnValue;
	}

}
