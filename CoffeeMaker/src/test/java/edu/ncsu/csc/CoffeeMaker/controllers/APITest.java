package edu.ncsu.csc.CoffeeMaker.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;

public abstract class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    protected abstract String getEndpoint ();

    @BeforeEach
    protected void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    protected <T> List<T> requestGetAllList ( final Class<T> objClass ) throws Exception {
        return TestUtils.asObjectList( mvc.perform( get( getEndpoint() ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString(), objClass );
    }

    protected <K, V> Map<K, V> requestGetAllMap ( final Class<K> keyClass, final Class<V> valueClass )
            throws Exception {
        return TestUtils.asObjectMap( mvc.perform( get( getEndpoint() ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString(), keyClass, valueClass );
    }

    protected ResultActions requestGet ( final String name ) throws Exception {
        return mvc.perform( get( new StringBuilder( getEndpoint() ).append( "/" ).append( name ).toString() ) );
    }

    protected <T> T requestGetSuccess ( final String name, final Class<T> objClass )
            throws UnsupportedEncodingException, Exception {
        return TestUtils.asObject(
                requestGet( name ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString(),
                objClass );
    }

    protected <T> ResultActions requestPost ( final T target ) throws Exception {
        return mvc.perform( post( getEndpoint() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( target ) ) );
    }

    protected <T> ResultActions requestPut ( final String name, final T update ) throws Exception {
        return mvc.perform( put( new StringBuilder( getEndpoint() ).append( "/" ).append( name ).toString() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( update ) ) );
    }

    protected ResultActions requestDelete ( final String name ) throws Exception {
        return mvc.perform( delete( new StringBuilder( getEndpoint() ).append( "/" ).append( name ).toString() ) );
    }
}
