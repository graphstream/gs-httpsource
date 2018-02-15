package org.graphstream.httpsource;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHTTPSource {
	private static final String NODE_1 = "/node/1";

	final static String ROOT_URL = "http://localhost:8080/g1";

	static HTTPSource source;

	static Graph graph;

	/**
	 * Create server and attach graph to it
	 */
	@BeforeClass
	public static void setUp() {
		TestHTTPSource.source = new HTTPSource("g1", 8080);
		TestHTTPSource.graph = new DefaultGraph("g1");
		TestHTTPSource.source.addSink(TestHTTPSource.graph);
	}

	@Test
	public void testAddAndDeleteNode() throws IOException {

		HttpUriRequest request = new HttpPost(TestHTTPSource.ROOT_URL + TestHTTPSource.NODE_1);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(200));
		MatcherAssert.assertThat(TestHTTPSource.graph.getNodeCount(), Is.is(1));
		request = new HttpDelete(TestHTTPSource.ROOT_URL + TestHTTPSource.NODE_1);
		httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(200));
		MatcherAssert.assertThat(TestHTTPSource.graph.getNodeCount(), Is.is(0));

	}

	@Test
	public void testAddAndDeleteEdge() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpPost(TestHTTPSource.ROOT_URL + TestHTTPSource.NODE_1);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(200));
		request = new HttpPost(TestHTTPSource.ROOT_URL + "/node/2");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(200));
		request = new HttpPost(TestHTTPSource.ROOT_URL + "/edge/1/1/2/false");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(200));
		MatcherAssert.assertThat(TestHTTPSource.graph.getNodeCount(), Is.is(2));
		MatcherAssert.assertThat(TestHTTPSource.graph.getEdgeCount(), Is.is(1));
		request = new HttpDelete(TestHTTPSource.ROOT_URL + "/edge/1");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(200));
	}

	@Test
	public void testStep() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpPost(TestHTTPSource.ROOT_URL + "/step/1");
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(200));
		request = new HttpPost(TestHTTPSource.ROOT_URL + "/step/A");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		MatcherAssert.assertThat(httpResponse.getStatusLine().getStatusCode(), Is.is(400));
	}

	@After
	public void cleanGraph() {
		TestHTTPSource.graph.clear();
	}

	/*
	 * Stop server
	 */
	@AfterClass
	public static void cleanUp() {
		TestHTTPSource.source.stop();
	}

}
