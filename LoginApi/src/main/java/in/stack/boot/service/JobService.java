package in.stack.boot.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.stack.boot.model.Job;
import in.stack.boot.repository.JobRepository;

@Service
public class JobService {

	@Autowired
	private JobRepository jobRepository;
	
	// Get Favorite Job List For User
	public ArrayList<String> getJobsByUserId(String userid) {
		return jobRepository.getJobByUser(userid);
	}
	
	// Save Job Detail
	public Job saveJobDetails(Job job) {
		return jobRepository.save(job);
	}
	
	// Delete Job Detail
	public void deleteJobDetails(Job job) {
		jobRepository.deleteJob(job.getJobid(), job.getUserid());
	}

	public Boolean isJobUserPairExists(Job job) {
		if(jobRepository.isFavoriterPairExists(job.getJobid(), job.getUserid()).isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean isUserExists(String userid) {
		if(jobRepository.getJobByUser(userid).isEmpty()) {
			return false;
		}
		return true;
	}


}
