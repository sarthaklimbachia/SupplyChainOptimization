package com.sjsu.CMPE281;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class SensorInformationServlet
 */
@WebServlet("/SensorInformationServlet")
public class SensorInformationServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SensorInformationServlet()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try
		{
			String jsonString = request.getParameter("req");
			JSONObject json = new JSONObject(jsonString);
			int noOfSensors = json.getInt("NO-OF-SENSORS");
			
			out.println("NO-OF-SENSORS::: " + noOfSensors);
			
			// establish connection with db
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://mysql-instance1.cfa3qxsmwzic.us-west-1.rds.amazonaws.com:3306/cmpe281", "cmpe281", "admin123");
			
			out.println("Connection established.....");
			
			for (int i = 0; i < noOfSensors; i++)
			{
				JSONObject data = json.getJSONObject("Sensor-" + i);

				out.println("data....." + data);
				
				String controlNodeId = data.getString("ControlNodeId");
				String truckID = data.getString("truckId");
				String sensorNodeId = data.getString("SensorNodeId");
				String sensorId = data.getString("SensorId");
				int sensorType = data.getInt("SensorType");
				String sensorData = data.getString("SensorData");
				long timeStamp = data.getLong("Timestamp");

				// save to db
				if (!con.isClosed())
				{
					// check if sensor-id already exists
					// Statement stmt = con.createStatement();
					// String query =
					// "SELECT SENSOR_ID FROM SENSOR_DATA WHERE SENSOR_ID=" +
					// sensorId;
					// ResultSet rs = stmt.executeQuery(query);
					// if (rs.next())
					// {
					// // as sensor already exists just update the
					// }

					PreparedStatement ps = ((java.sql.Connection) con).prepareStatement("INSERT INTO SENSOR_DATA(SENSOR_ID, SENSOR_NODE_ID, SENSOR_CONTROL_NODE_ID, TRUCK_ID, TYPE, SENSOR_DATA, TIME_STAMP) VALUES (?,?,?,?,?,?,?)");

					out.println("adding to db.....");
					ps.setString(1, sensorId);
					ps.setString(2, sensorNodeId);
					ps.setString(3, controlNodeId);
					ps.setString(4, truckID);
					ps.setInt(5, sensorType);
					ps.setString(6, sensorData);
					ps.setLong(7, timeStamp);
					ps.execute();

					out.println("Inserted");
				} else
				{
					out.println("Connection failed");
				}
			}
			
			con.close();
		} catch (Exception e1)
		{
			out.println(e1);
		}

		out.close();
	}
}
