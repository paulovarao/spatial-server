package com.varaodev.spatialserver.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.varaodev.spatialserver.serializers.ObjectSerializer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class ResourceTests implements ObjectSerializer {
	
	@LocalServerPort
	protected int port;
	
	@Autowired
	protected MockMvc mockMvc;
	
	protected String endpoint = "http://localhost:" + port + "/" + domain()+ "/";
	
	protected final String error = "error";
	protected abstract String domain();
	
	protected MvcResult performMock(RequestBuilder builder, ResultMatcher status) 
			throws Exception {
		return mockMvc.perform(builder)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status)
	            .andReturn();
	}
	
	protected MockHttpServletRequestBuilder defaultBuilder(String resource) {
		return post(endpoint + resource).contentType(MediaType.APPLICATION_JSON);
	}

}
