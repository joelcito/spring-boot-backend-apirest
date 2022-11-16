package com.bolsadeideas.springboot.backend.apirest.models.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UploadFileServiceImpl implements IUploadFileService {
	
	private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);
	
	private final static String DIRECTORIO_UPLOAD = "uploads";

	@Override
	public Resource cargar(String nombreFoto) throws MalformedURLException {
				
		Path rutaFotoAnterior = getPath(nombreFoto);
		log.info(rutaFotoAnterior.toString());
		
		Resource recurso = new UrlResource(rutaFotoAnterior.toUri());
				
		if(!recurso.exists() && !recurso.isReadable()) {
			
			rutaFotoAnterior = Paths.get("src/main/resources/static/images").resolve("no-user.jpg").toAbsolutePath();
			
			recurso = new UrlResource(rutaFotoAnterior.toUri());

			
			log.error("Error no se puede cargar la imagen con el nombre de la foto");
			
			//throw new RuntimeException("Error no se puede cargar la imagen con el nombre de la foto");
		}
		
		
		return recurso;
	}

	@Override
	public String copiar(MultipartFile archivo) throws IOException {
				
		//sacamos el nombre del archivo
		String nombreArchivo = UUID.randomUUID()+"_"+archivo.getOriginalFilename().replace(" ","");
		//le damos la ruta origina donde se guardara el archivo subido con el nombre del archivo
		Path rutaArchivo = getPath(nombreArchivo);
		
		log.info(nombreArchivo.toString());
		
		Files.copy(archivo.getInputStream(), rutaArchivo);		
		
		return nombreArchivo;
	}

	@Override
	public boolean eliminar(String nombreFoto) {
		
		if(nombreFoto != null && nombreFoto.length() > 0) {
			Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();	
			File archivoFotoAnterior = rutaFotoAnterior.toFile();
			if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Path getPath(String nombreFoto) {
		// TODO Auto-generated method stub
		return Paths.get(DIRECTORIO_UPLOAD).resolve(nombreFoto).toAbsolutePath();
	}

}
