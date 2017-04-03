/*******************************************************************************
 * Copyright (c) 2014, 2017  IBM Corporation, Carnegie Mellon University and others
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package hulop.sample.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

/**
 * Servlet implementation class ServiceServlet
 */
@WebServlet("/service")
public class ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String API_KEY = "sample_api_key";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String api_key = request.getParameter("api_key");
		if (!API_KEY.equals(api_key)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		String lang = request.getParameter("lang");
		String id = request.getParameter("id");
		String input_text = request.getParameter("text");
		if (id == null) {
			id = request.getSession(true).getId();
		}
		String agent_name = "ja".equals(lang) ? "花子" : "Mary";
		String output_text;
		if (input_text == null || input_text.isEmpty()) {
			output_text = "ja".equals(lang) ? "何かお話ください" : "Speak something";
		} else {
			output_text = String.format("ja".equals(lang) ? "了解\n%s" : "OK\n%S", input_text);
		}
		try {
			JSONObject input = new JSONObject();
			if (input_text != null) {
				input.put("text", input_text);
			}

			JSONObject output = new JSONObject();
			output.put("text", new JSONArray(output_text.split("\n")));
			output.put("log_messages", new JSONArray());
			output.put("nodes_visited", new JSONArray());

			JSONObject context = new JSONObject();
			context.put("conversation_id", "");
			context.put("agent_name", agent_name);

			JSONObject result = new JSONObject();
			result.put("output", output);
			result.put("input", input);
			result.put("context", context);
			result.put("entities", new JSONArray());
			result.put("intents", new JSONArray());
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			OutputStream os = null;
			try {
				(os = response.getOutputStream()).write(result.toString().getBytes("UTF-8"));
			} finally {
				if (os != null) {
					os.close();
				}
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
