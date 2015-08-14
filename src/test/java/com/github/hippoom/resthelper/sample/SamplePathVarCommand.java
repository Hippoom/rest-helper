package com.github.hippoom.resthelper.sample;

import com.github.hippoom.resthelper.annotation.PathVar;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class SamplePathVarCommand {
    @PathVar("id")
    @NotBlank
    private String id;

    @PathVar("foo")
    @NotBlank
    private String foo;

    @PathVar("bar")
    @NotBlank
    @Length(min = 1, max = 4)
    private String bar;

    @NotBlank
    private String content;

    public String getId() {
        return id;
    }

    public String getFoo() {
        return foo;
    }

    public String getBar() {
        return bar;
    }

    public String getContent() {
        return content;
    }
}
