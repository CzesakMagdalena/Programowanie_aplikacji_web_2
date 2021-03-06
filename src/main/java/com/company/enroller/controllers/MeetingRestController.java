package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;
import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;

@RestController
@RequestMapping("/meetings")

public class MeetingRestController {

	@Autowired
	MeetingService meetingService;
	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	// GET localhost:8080/meetings
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	// GET localhost:8080/meetings/2
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findByID(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	// POST localhost:8080/meetings
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
		if (meetingService.findByID(meeting.getId()) != null) {
			return new ResponseEntity<String>("Unable to create. Meeting withID'" + meeting.getId() + "'already exists",
					HttpStatus.CONFLICT);
		}

		meetingService.add(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
	}

	// DELETE localhost:8080/meetings/2
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> geMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findByID(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingService.delete(meeting);
		// return new ResponseEntity<Participant>(participant, HttpStatus.OK);
		return new ResponseEntity<Participant>(HttpStatus.NO_CONTENT);
	}
	// GET localhost:8080/meeting/3/participants <- informacje o spotkaniu i
	// uzytkownikach

	// GET localhost:8080/meetings/2/participants
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findByID(id);
		if (meeting == null) {
			return new ResponseEntity<String>("Not find meeting with id " + id, HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meeting.getParticipants();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.POST)

	public ResponseEntity<?> addMeetingParticipant(@PathVariable("id") long id, @PathVariable("login") String login) {
		Meeting meeting = meetingService.findByID(id);
		if (meeting == null) {
			return new ResponseEntity("Not find meeting with id " + id, HttpStatus.NOT_FOUND);
		}
		
		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity("No participant found with login" + login,
					HttpStatus.NOT_FOUND);
		}
		
		meeting.addParticipant(participant);
		meetingService.update(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);

	}

	// PUT localhost:8080/
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)

	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
		Meeting foundMeeting = meetingService.findByID(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		foundMeeting.setTitle(meeting.getTitle());
		foundMeeting.setDescription(meeting.getDescription());
		foundMeeting.setDate(meeting.getDate());
		meetingService.update(foundMeeting);

		return new ResponseEntity<Meeting>(foundMeeting, HttpStatus.OK);
	}

	// DELETE localhost:8080/user
	@RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)

	public ResponseEntity<?> removeMeetingParticipant(@PathVariable("id") long id,
			@PathVariable("login") String login) {
		Meeting meeting = meetingService.findByID(id);
		if (meeting == null) {
			return new ResponseEntity("Not find meeting with id " + id, HttpStatus.NOT_FOUND);
		}

		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity("No participant in this meeting " + login,
					HttpStatus.NOT_FOUND);
		}

		meeting.removeParticipant(participant);
		meetingService.update(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);

	}
}
