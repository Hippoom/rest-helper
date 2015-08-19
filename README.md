# rest-helper [![Build Status](https://travis-ci.org/Hippoom/rest-helper.svg?branch=master)](https://travis-ci.org/Hippoom/rest-helper)

## Latest Release

## Why

Both Spring-MVC and Jersey provide annotations and parser to fetch context information in url pattern which is very convenient.
But sometimes, you have to inject the path variables into your request body object manually, just like this:

```` java
    //Spring example:      
    @RequestMapping(value = "/{foo}", method = PUT)
    @ResponseBody
    protected HttpEntity<FooRepresentation> update(@PathVariable("foo") String foo, @RequestBody EditFooCommand command) {
        command.setFoo(foo); // this is boring
        fooCommandHandler.handle(command)
        //assemble foo representation
    }
````

Actually, the framework should do the work for developers and it is easy to extend spring's parser. 
But I don't want to copy and past the extension for every project. 
So that's where rest-helper come into play.


## Quick Start

Let's say you have a command and some of its attributes should come from path variables.
What you need to do is annotate the attributes with PathVar and declare the method parameter with @Command

```` java
    import com.github.hippoom.resthelper.annotation.PathVar;
    
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
    }
    
    import com.github.hippoom.resthelper.annotation.Command;
    
    @RequestMapping("/command")
    public class SampleCommandController {
    
        @RequestMapping(value = "/{id}/foo/{foo}/bar/{bar}", method = PUT)
        @ResponseBody
        public SamplePathVarCommand hello(@Valid @Command SamplePathVarCommand command) {
            return command;
        }
````

Last but not the least is to add the ArgumentResolver to your application, with Annotated config:

````java

    @Configuration
    public class ServletContext extends WebMvcConfigurerAdapter {
    
        @Autowired
        private BeanFactory beanFactory;
    
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new CommandMethodArgumentResolver(beanFactory));
            return;
        }
    }
````

with xml config:

````xml
    <bean class="org.springframework.web.servlet.mvc.annotation.
          AnnotationMethodHandlerAdapter"
        <property name="customArgumentResolver">
            <bean class="com.github.hippoom.resthelper.spring.CommandMethodArgumentResolver">
        </property>
    </bean>
````

or:

````xml
    <mvc:annotation-driven>
		<mvc:argument-resolvers>
			<bean class="com.github.hippoom.resthelper.spring.CommandMethodArgumentResolver"/>
		</mvc:argument-resolvers>
	</mvc:annotation-driven>
````

You can add @javax.validation.Valid to protect your command as rest-helper will inject the path variables before the validation.

## Contributing
Any suggestion and pull request is welcome.

## License

Licensed under Apache License; You may obtain a copy of the License in the LICENSE file, or at [here](LICENSE).