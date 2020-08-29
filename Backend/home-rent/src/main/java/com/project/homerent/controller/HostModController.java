package com.project.homerent.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.homerent.model.dto.MyHomeDto;
import com.project.homerent.model.dto.MyHomePostDto;
import com.project.homerent.model.usermodel.User;
import com.project.homerent.service.HostService;
import com.project.homerent.service.ImageService;
import com.project.homerent.service.UserService;
import com.project.homerent.util.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static com.project.homerent.util.Helpers.convertToJson;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/host")
@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
public class HostModController {

    @Autowired
    private HostService hostService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;
    //TODO gia ola: tsekare an to approved einai 1 se ka8e API, an den einai den exei ginei approved apo admin

    @GetMapping("/{id}/homes")
    //TODO rwtise tin xristina an tin voleuei na stelnei to token h an thn voleuei me id
//    public ResponseEntity<String> getHostHomes(@RequestBody Principal principal) throws JsonProcessingException {
    public ResponseEntity<String> getHostHomes(@PathVariable("id") Long id)  throws JsonProcessingException {
//        User user = userService.findByUsername(principal.getName());
        User user = userService.findById(id);

        if(user.getApproved()==0)
            return ResponseEntity.ok().body("{\"message\": \"Host Isn't approved yet by administrator\"}");
        else
            return ResponseEntity.ok().body(convertToJson(hostService.findByUserId(id)));
//            return ResponseEntity.ok().body(Helpers.convertToJson(hostService.findByUserId(user.getId())));
    }

    @PostMapping("/home/new")
    public ResponseEntity<String> createHome(@RequestBody MyHomeDto myHomePostDto, Principal principal) throws Exception {
        User user = userService.findByUsername(principal.getName());
        if(user.getRoles().stream().findFirst().isPresent())
            return ResponseEntity.ok().body(convertToJson(hostService.save(myHomePostDto)));
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"Status\": \"Error\"}");
    }

    @PostMapping("home/{id}/image")
    public String handleImagePost(@PathVariable String id, @RequestParam("imagefile") MultipartFile file){

        imageService.saveImageFileToHome(Long.valueOf(id), file);

        return "redirect:/home/" + id + "/show";
    }
}