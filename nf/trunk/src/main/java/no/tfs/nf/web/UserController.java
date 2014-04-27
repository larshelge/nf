package no.tfs.nf.web;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static no.tfs.nf.util.CollectionUtilsWrapper.sort;

import java.util.Arrays;

import no.tfs.nf.api.User;
import no.tfs.nf.api.UserService;
import no.tfs.nf.comparator.UserNameComparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Lars Helge Overland
 */
@Controller
public class UserController
{
    @Autowired
    private UserService userService;
    
    @RequestMapping("/user")
    @Secured({"ROLE_ADMIN"})
    public ModelAndView getUser( @RequestParam(value="id",required=false) Integer id )
    {
        ModelAndView mav = new ModelAndView( "maintenance/user" ).addObject( "userroles", Arrays.asList( User.USERROLES ) );
        
        if ( id != null )
        {
            mav.addObject( "user", userService.get( id ) );
        }
        
        return mav;
    }

    @RequestMapping("/saveUser")
    @Secured({"ROLE_ADMIN"})
    public String saveUser( @ModelAttribute("user") User user, BindingResult result )
    {
        user.setPassword( userService.encodePassword( user.getPassword() ) );
        
        if ( user.getId() > 0 )
        {
            userService.update( user );
        }
        else
        {
            userService.save( user );
        }
        
        return "forward:users";
    }
    
    @RequestMapping("/deleteUser")
    @Secured({"ROLE_ADMIN"})
    public String deleteUser( @RequestParam Integer id )
    {
        userService.delete( userService.get( id ) );
        
        return "forward:users";
    }

    @RequestMapping("/users")
    @Secured({"ROLE_ADMIN"})
    public ModelAndView getAllUsers()
    {
        return new ModelAndView( "maintenance/users" ).addObject( "users", sort( userService.getAll(), new UserNameComparator() ) );
    }

    @RequestMapping("/registerUser")
    public String registerUser( @ModelAttribute("user") User user, BindingResult result )
    {
        user.setPassword( userService.encodePassword( user.getPassword() ) );
        user.setUserrole( User.USERROLES[0] );
        
        if ( user.getId() > 0 )
        {
            userService.update( user );
        }
        else
        {
            userService.save( user );
        }
        
        return "forward:login";
    }
    
    @RequestMapping("/usernameAvailable")
    public @ResponseBody Boolean usernameAvailable( @RequestParam String username )
    {
        return userService.getByUsername( username ) == null;
    }
    
    @RequestMapping("/requestUserRestore")
    public String requestUserRestore( @RequestParam String username )
    {
        userService.requestUserRestore( username );
        
        return "forward:login";
    }
    
    @RequestMapping("/userRestore")
    public ModelAndView userRestore( @RequestParam String username, @RequestParam String restoreCode )
    {
        boolean canRestore = userService.canRestoreUser( username, restoreCode );
        
        if ( canRestore )
        {
            return new ModelAndView( "userRestore" ).addObject( "username", username ).addObject( "restoreCode", restoreCode );
        }
        else
        {
            return new ModelAndView( "login" );
        }
    }
    
    @RequestMapping("/restoreUser")
    public String restoreUser( @RequestParam String username, @RequestParam String restoreCode, @RequestParam String password )
    {
        userService.restoreUser( username, restoreCode, password );
        
        return "forward:login";
    }
}
