package com.bolsadeideas.springboot.backend.apirest.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Region;

/*public interface IClienteDao extends CrudRepository<Cliente, Long>{*/

//esto es para paginacions
public interface IClienteDao extends JpaRepository<Cliente, Long>{
	
	
	//Para el listado de Regiones de la llave forenana
	//Query se utiliza para las personalizaciones de los query
	@Query("from Region")
	public List<Region> findAllRegiones();
}
