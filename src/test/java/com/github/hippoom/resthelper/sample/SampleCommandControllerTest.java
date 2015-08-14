package com.github.hippoom.resthelper.sample;

import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")//random port used
public class SampleCommandControllerTest {
    @Value("${local.server.port}")
    private int port;

    protected String getBaseUri() {
        return RestAssured.DEFAULT_URI;
    }

    protected int getPort() {
        return port;
    }

    @Before
    public void config_rest_assured() {
        RestAssured.baseURI = getBaseUri();
        RestAssured.port = getPort();
    }

    @Test
    public void itShouldBindPathVariablesToCommandFields() throws Exception {

        final String command = "{" +
                "\"content\": \"123456\"" +
                "}";

        Response response = given().contentType(JSON).content(command).
                when().
                put("/command/1/foo/f/bar/b").
                then().log().everything().
                statusCode(SC_OK).
                extract().response();

        String responseBody = response.asString();

        assertThat(JsonPath.read(responseBody, "$.id"), equalTo("1"));
        assertThat(JsonPath.read(responseBody, "$.foo"), equalTo("f"));
        assertThat(JsonPath.read(responseBody, "$.bar"), equalTo("b"));
        assertThat(JsonPath.read(responseBody, "$.content"), equalTo("123456"));
    }

    @Test
    public void itShouldReturnBadRequest_givenInvalidPathVariableBound() throws Exception {

        final String command = "{" +
                "\"content\": \"123456\"" +
                "}";

        given().contentType(JSON).content(command).
                when().
                put("/command/1/foo/f/bar/tooLong").
                then().log().everything().
                statusCode(SC_BAD_REQUEST);

    }


}
