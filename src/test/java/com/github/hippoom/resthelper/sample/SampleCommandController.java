package com.github.hippoom.resthelper.sample;

import com.github.hippoom.resthelper.annotation.Command;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/command")
public class SampleCommandController {

    @RequestMapping(value = "/{id}/foo/{foo}/bar/{bar}", method = PUT)
    @ResponseBody
    public SamplePathVarCommand hello(@Valid @Command SamplePathVarCommand command) {
        return command;
    }
}
