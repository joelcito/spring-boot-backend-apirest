package com.bolsadeideas.springboot.backend.apirest.controllers;

//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Region;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;
import com.bolsadeideas.springboot.backend.apirest.models.services.IUploadFileService;

/*importamos la clases como es api rest controllerss*/
@RestController

/*Damos los permisos a dominios esa le damos permiso para que se conecte con angular*/
@CrossOrigin(origins = {"http://localhost:4200/"})

/*Para que jale los apis de los mapeos*/
@RequestMapping("/api")
public class ClienteRestController {

	/*Inyectamos al interfaces*/
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	/*Maoemaos el metodo*/
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}
	
	/*Maoemaos el metodo*/
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){
		Pageable pageable = PageRequest.of(page, 4);
		return clienteService.findAll(pageable);
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){
		//manejos de errores de servidor (Base da datos SQL, )
		Cliente cliente = null;
		
		Map<String, Object> response = new HashMap<>();
		try {
			cliente = clienteService.findById(id);	
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al consultar la base de datos!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause());
			// DEvolvemos el error del neuvo mapa			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(cliente == null) {
			response.put("mensaje", "El cliente con el Id "+id.toString()+" no esta registrado en la base de datos!");
			// DEvolvemos el error del neuvo mapa			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
			
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK); 
		//return clienteService.findById(id);
	}
	
	@PostMapping("/clientes")
	//ahroa ya lo enviaremos desde la funcion @ResponseStatus(HttpStatus.CREATED)
	//@Valid eso es para validar el formulario, BindingResult para devolver el resultado de la validacion
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		
		Cliente clienteNew = null;
		//este es mapeo de respuesta de errores
		Map<String, Object> response = new HashMap<>();
		
		
		//para los validadores del formulario
		if(result.hasErrors()) {
			
			// segunda forma
			List<String> errors = result.getFieldErrors()
										.stream()
										.map(err -> {
											return "El campo '"+err.getField()+"' "+err.getDefaultMessage()	;
										})
										.collect(Collectors.toList());
			
			//primera forma
			/*List<String> errors = new ArrayList<>();
			
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campo '"+err.getField()+"'"+err.getDefaultMessage());
			}
			*/
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST); 
		}
		
		
		try {
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al consultar la insercon de datos la base de datos!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause());	
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		//mandamos mensaje de aceptacion de que se creo el cliente
		response.put("mensaje", "El clinete fue creado con exito");
		response.put("cliente", clienteNew);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		//return clienteService.save(cliente);
	}
	
	@PutMapping("/clientes/{id}")
	// @ResponseStatus(HttpStatus.CREATED) ahora responderesmos lo que queramos
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		
		Cliente clienteActual  = clienteService.findById(id);
		
		Cliente clienteUpDate  = null;
		
		//este es mapeo de respuesta de errores
		Map<String, Object> response = new HashMap<>();
		
		//para los validadores del formulario
		if(result.hasErrors()) {
			
			// segunda forma
			List<String> errors = result.getFieldErrors()
										.stream()
										.map(err -> {
											return "El campo '"+err.getField()+"' "+err.getDefaultMessage()	;
										})
										.collect(Collectors.toList());
			
			//primera forma
			/*List<String> errors = new ArrayList<>();
			
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campo '"+err.getField()+"'"+err.getDefaultMessage());
			}
			*/
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST); 
		}
		
		if(clienteActual == null){
			response.put("mensaje", "El cliente con el Id "+id.toString()+" no esta registrado en la base de datos!");
			// DEvolvemos el error del neuvo mapa			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}		
		
		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			clienteActual.setRegion(cliente.getRegion());
			
			clienteUpDate = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al consultar la edicion en la base de datos!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause());	
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
				
		response.put("mensaje", "El cliente se actualizo con exito!");
		response.put("cliente", clienteUpDate);		
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
	}
	
	@DeleteMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		//este es mapeo de respuesta de errores
		Map<String, Object> response = new HashMap<>();
			
		try {
			
			Cliente cliente = clienteService.findById(id);
			
			//Verificamos si existe la foto
			String nombreFotoAnterior = cliente.getFoto();
			
			uploadFileService.eliminar(nombreFotoAnterior);
			
			clienteService.delete(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al aliminar el cliente!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause());	
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Se elimino con Exito!");
			 
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		
		Map<String, Object> response = new HashMap<>();
		
		Cliente cliente = clienteService.findById(id);
		
		if(!archivo.isEmpty()){		
			
			String nombreArchivo = null;
			
			try {
				nombreArchivo = uploadFileService.copiar(archivo);	
			} catch (Exception e) {
				response.put("mensaje", "Error al subir la imagen!");
				response.put("error", e.getMessage()+": "+e.getCause().getMessage());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);	
			}
			
			//METOD DE UDEMY
			//Verificamos si existe la foto
			String nombreFotoAnterior = cliente.getFoto();
			
			uploadFileService.eliminar(nombreFotoAnterior);
					
			/*
			// Este es mi metodo de JOEL y sirve
			//buscamos el archivo
			if(cliente.getFoto() != null){
				String file_name = "uploads/"+cliente.getFoto();
				Path path = Paths.get(file_name);
		        try {
			        boolean haber =  Files.deleteIfExists(path);				
				} catch (Exception e) {
					// TODO: handle exception
				}		
			}
			*/
						
			//seteamos el nombre del archio
			cliente.setFoto(nombreArchivo);
			
			//actualizamos el cliente
			clienteService.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje", "Se actulizo la foto del cliente con EXITO!: "+nombreArchivo);
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
				
		Resource recurso = null;
		
		try {
			recurso = uploadFileService.cargar(nombreFoto);	
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+recurso.getFilename()+"\"");
		
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
	
		/*Maoemaos el metodo*/
	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones(){
		return clienteService.findAllRegiones();
	}
 }
