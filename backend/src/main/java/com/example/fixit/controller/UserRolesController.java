package com.example.fixit.controller;

import com.example.fixit.model.UserRoles;
import com.example.fixit.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user-roles")
public class UserRolesController {

    @Autowired
    private UserRolesService userRolesService;

    //  TODO
}