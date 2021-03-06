package in.stack.boot.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.stack.boot.model.Job;
import in.stack.boot.service.JobService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping ("/favorites/api/v1")
public class JobController {

		@Autowired
		private JobService jobService;
		
		// Search Favorite Jobs
		@GetMapping("/users/{userid}")
//		@CrossOrigin(origins = "http://localhost:4200")
		public ResponseEntity<ArrayList<String>> getFavoriteJobsForUsers(@PathVariable String userid){
			
			// TestCase 1 : check if job id already exits
			if(jobService.isUserExists(userid)) {	
				ArrayList<String> joblist = jobService.getJobsByUserId(userid);
				return ResponseEntity.ok(joblist);	// 200 All Okay
			}
			return ResponseEntity.badRequest().build(); // 400 Not Found		
		}
		
		// Save Favorite Job
		@PostMapping("/jobs/save")
//		@CrossOrigin(origins = "http://localhost:4200")
		public ResponseEntity<Job> saveFavoriteJobForUser(@RequestBody Job job) {
				
			// TestCase 1 : check if job id already exits
			if(jobService.isJobUserPairExists(job)) {
				return new ResponseEntity<> ( HttpStatus.CONFLICT); // 409 Already Exits
			}
			
			// TestCase 2 : check if values are null 
			if((job.getJobid() == "") || (job.getUserid() == "")) {
				return new ResponseEntity<> (HttpStatus.BAD_REQUEST); // 400 Bad Input	
			}
						
			// TestCase 3 : user id (email) validation using REGEX 
						
			// Save Favorite job  
			jobService.saveJobDetails(job);
			
			// Response 
			return ResponseEntity.ok(job); // 200 All Okay
		}		
		
		// Delete Favorite Job
		@DeleteMapping("/jobs/delete")
//		@CrossOrigin(origins = "http://localhost:4200")
		public ResponseEntity<String> deleteFavoriteJobForUser(@RequestBody Job job) {

			// TestCase 1 : check if job id already exits
			if(jobService.isJobUserPairExists(job)) {
				// Delete job object 
				jobService.deleteJobDetails(job);
				return new ResponseEntity<> (HttpStatus.OK); // 200 All Okay
			}			
			return new ResponseEntity<> (HttpStatus.BAD_REQUEST); // 400 Bad Input
		}

}