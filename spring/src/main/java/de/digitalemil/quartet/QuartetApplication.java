package de.digitalemil.quartet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

// CREATE DATABASE mydb COLOCATED=true;
@SpringBootApplication
@RestController
public class QuartetApplication {
	@Autowired
	DataSource ds;
	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(QuartetApplication.class, args);
	}

	@PostMapping("/execute")
	public String execute(@RequestBody String body) {
		String result= "";
		try {
			String sql= new JSONObject(body).getString("sql");
			List<Map<String, Object>> res= null;
			try {
				res= jdbcTemplate.queryForList(sql);
			}
			catch (Exception e) {

			}
			for (int i = 0; res!= null && i < res.size(); i++) {
				Iterator<Object> le= res.get(i).values().iterator();
			
		        while (le.hasNext()) {
					String value= "";
					try {
						value= le.next().toString();
						System.out.println("value= " + value);
						result+= value;	
					}
					catch(Exception e) {
					}
        			if(le.hasNext())
					result+= ", ";
        		}
				result+= "\n";
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	    
		System.out.println("Result: "+result);
		return result;
	}

	private String get(String key, JSONObject jobj, String defaultvalue) {
		if(!jobj.has(key))
			return defaultvalue;
		else
			return jobj.getString(key);
	}

	@PostMapping("/datasource")
	public String datasource(@RequestBody String body) {
		JSONObject jobj= new JSONObject(body);

		String type= get("type", jobj, "postgres");
		String ip = get("ip", jobj, "127.0.0.1");
		String user = get("user", jobj, "yugabyte");
		String password = get("password", jobj, "yugabyte");
		String dbname = get("dbname", jobj, "testdb");
		String port = get("port", jobj, "5433");
		String endpoints = get("endpoints", jobj, "");
		String poolsize= get("poolsize", jobj, "16");
		String geo= get("geo", jobj, "");

		DataSource ds = null;

		if (type.equals("postgres")) {
			System.out.println("Using Postgres Driver.");
			ds = new SpringJdbcConfig().dataSource(ip, port, user, password, dbname);
		} else {
			System.out.println("Using Yugabyte Smartdriver.");
			ds = new SpringJdbcConfig().ybDataSource(ip, port, user, password, dbname, endpoints, poolsize, geo);
		}
		jdbcTemplate.setDataSource(ds);
		try {
			System.out.println("JDBC URL: " + jdbcTemplate.getDataSource().getConnection().getMetaData().getURL());
		} catch (Exception e1) {
			System.err.println(e1);
		}
		return "Datasource created.";
	}

}
