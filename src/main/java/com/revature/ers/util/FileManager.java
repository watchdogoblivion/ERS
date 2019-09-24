package com.revature.ers.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

import com.revature.ers.models.Resource;

public class FileManager {
	private static final Logger LOGGER = Logger.getLogger(FileManager.class);
	public static final String fileSystemStorageSimulation = "C:\\Users\\Samuel\\Documents\\Revature\\batch-source\\expense-reimbursment-system\\src\\main\\webapp\\static\\fileSystemStorageSimulation";
	public static final String staticPath = "static";
	
	public List<File> getFiles(HttpServletRequest request, String root) {
		root = request.getServletContext().getRealPath(root);
		List<File> files = null;
		try {
			files = Files.walk(Paths.get(root)).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return files;
	}

	public List<String> getDirectories(HttpServletRequest request, String root, int depth) {
		final String finalRoot = request.getServletContext().getRealPath(root);
		List<String> files = null;
		try {
			files = Files.walk(Paths.get(finalRoot), depth).filter(Files::isDirectory).map((Path p) -> {
				String d = p.toString();
				return d.substring(d.lastIndexOf(root));
			}).collect(Collectors.toList());
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return files;
	}

	public String getFileContent(HttpServletRequest request, String filePath) {
		filePath = request.getServletContext().getRealPath(filePath);
		String resource = "";
		try {
			resource = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return resource;
	}

	public String getFileContentRealPath(String filePath) {
		String resource = "";
		try {
			resource = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return resource;
	}

	public Map<String, Resource> getHTMLCSSJSResources(HttpServletRequest request, String root, String[] subs) {
		Map<String, String> directoryUris = ResourceURIs.getDIRECTORYURIS(request, root, 1);
		Map<String, Resource> resources = new HashMap<>();
		for (String sub : subs) {
			List<File> fileList = getFiles(request, directoryUris.get(sub));
			for (File file : fileList) {
				String key = file.getName().substring(0, file.getName().indexOf('.'));
				String field = getFileContentRealPath(file.getAbsolutePath());
				switch (sub.toUpperCase()) {
				case "HTML":
					Resource resource = new Resource(field, null, null);
					resource.setTitle(key.substring(0, 1).toUpperCase() + key.substring(1));
					resources.put(key, resource);
					break;
				case "CSS":
					resources.computeIfPresent(key, (k, r) -> {
						if (r != null) {
							r.setCss(field);
						}
						return r;
					});
					break;
				case "JS":
					resources.computeIfPresent(key, (k, r) -> {
						if (r != null) {
							r.setJavascript(field);
						}
						return r;
					});
					break;
				default:
				}
			}
		}
		return resources;
	}
	
	public void saveImageFile(HttpServletRequest request, Part filePart, String imageProfileUrl) {
		try(InputStream fileContent = filePart.getInputStream();){
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fileContent.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			byte[] data = result.toByteArray();
			try(ByteArrayInputStream bis = new ByteArrayInputStream(data);){
				BufferedImage bImage2 = ImageIO.read(bis);
				ImageIO.write(bImage2, "jpg", new File(imageProfileUrl));
			}
		}catch(IOException e){
			LOGGER.error(e);
		}
	}
	
	public byte [] getImageUriBytes(HttpServletRequest request, HttpServletResponse response, String url) {
		ServletContext cntx = request.getServletContext();
		byte [] bytes = null;
		try {
			// retrieve mimeType dynamically
			String mime = cntx.getMimeType(url);
			if (mime == null) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return bytes;
			}
			response.setContentType(mime);
			File file = new File(url);
			response.setContentLength((int) file.length());

			FileInputStream in = new FileInputStream(file);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = in.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}
			

			buffer.close();
			in.close();
			return buffer.toByteArray();
		} catch (Exception e) {
			LOGGER.info(e);
		}
		return bytes;
	}
}
