package com.bolsadeideas.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;


/*
	En otros casos se pude omitir si es que la tabla se llama el lo mismo que el entity
	@Table(.....)
 * 
 */
@Entity
@Table(name = "clientes")
public class Cliente implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "no puede estar vacio")
	@Size(min = 4,max = 15, message = "el tamaño debe estar entre 4 y 12")
	@Column(nullable = false)
	private String nombre;
	
	@NotEmpty(message = "no puede estar vacio")
	private String apellido;
	
	@NotEmpty(message = "no puede estar vacio")
	@Email(message = "debe ser una dirección de correo electrónico con formato correcto")
	@Column(nullable = false, unique = true)
	private String email;
	
	@NotNull(message = "el campo fecha no puede estar vacio")
	@Column(name = "created_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	
	private String foto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	@NotNull(message = "El clienite tiene que tener region")
	private Region region;
	
	/*Con eso tenemos las fecha de forma autmatia en la tabla clientes*/
	/*@PrePersist
	public void prePersisit() {
		createAt = new Date();
	}
	*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region_id) {
		this.region = region_id;
	}





	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
