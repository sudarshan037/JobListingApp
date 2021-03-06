package in.stack.boot.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.stack.boot.exception.UserNotFoundException;
import in.stack.boot.model.User;
import in.stack.boot.service.SecurityTokenGenerator;
import in.stack.boot.service.UserService;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private SecurityTokenGenerator tokenGen;
	
	private final String UPLOAD_DIRECTORY = "/home/sudarshan.choudhary/Downloads/";
	
	
	@PostMapping("/register")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<?> registerUser(@RequestBody User user){
		try {
			userService.saveUser(user);
			return new ResponseEntity<User>(user,HttpStatus.CREATED);
		}catch(Exception e) {
			return new ResponseEntity<String>("message: "+e.getMessage(),HttpStatus.CONFLICT);
		}
	}
	
	@PostMapping("/login")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<?> login(@RequestBody User loginInfo){
		try {
			if(loginInfo.getUserId()==null||loginInfo.getPassword()==null) {
				throw new UserNotFoundException("UserId or password cannot be empty");
			}
			User user = userService.findByUserIdAndPassword(loginInfo.getUserId(),loginInfo.getPassword());
			Map<String,String> map = tokenGen.generateToken(user);
			return new ResponseEntity<Map<String,String>>(map,HttpStatus.OK);
		}catch(UserNotFoundException e) {
			return new ResponseEntity<String>("message:"+e.getMessage(),HttpStatus.UNAUTHORIZED);
					
			}
	}
	
	@PostMapping("/uploadimage")
	@CrossOrigin(origins = "http://localhost:4200")
	//@ApiOperation(value="User Profile Picture Upload", notes="This api is responsible for profile picture upload for specific userid ")
	public ResponseEntity<String> uploadimage(@RequestParam("file") MultipartFile file,
			@RequestParam("userName") String userName) {

		try {
			if (file.isEmpty()) {
				System.out.println("bad req");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image Not Found !!");
			}

			// Save Image at Server
			String serverImageStorageUrl = UPLOAD_DIRECTORY + File.separator + userName + "." + file.getOriginalFilename().split("\\.(?=[^\\.]+$)")[1];

			Files.copy(file.getInputStream(), Paths.get(serverImageStorageUrl), StandardCopyOption.REPLACE_EXISTING);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body("Image Reciedved !!");
	}
	
	@RequestMapping(value = "/downloadimage/{userName}", method = RequestMethod.GET) 
	@CrossOrigin(origins = "http://localhost:4200")
	//@ApiOperation(value="User Profile Picture Download", notes="This api is responsible for profile picture download for specific userid ")
	public ResponseEntity<Object> downloadFile(@PathVariable String userName) throws IOException  {
	      String filename = UPLOAD_DIRECTORY + userService.getImageUrl(userName);;
	      File file = new File(filename);
	      InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	      HttpHeaders headers = new HttpHeaders();
	      
	      headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
//	      headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//	      headers.add("Pragma", "no-cache");
//	      headers.add("Expires", "0");
	      
	      ResponseEntity<Object> 
	      responseEntity = ResponseEntity.ok().headers(headers).contentLength(
	         file.length()).contentType(MediaType.parseMediaType("application/txt")).body(resource);
	      
	      return responseEntity;
	   }
	
}
