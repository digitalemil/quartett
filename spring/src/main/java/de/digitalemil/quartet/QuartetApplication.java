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
			List<Map<String, Object>> res= jdbcTemplate.queryForList(sql);
			for (int i = 0; i < res.size(); i++) {
				Iterator<Object> le= res.get(i).values().iterator();
			
		        while (le.hasNext()) {
					String value= le.next().toString();
        			System.out.println("value= " + value);
					result+= value;
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

	private String get(String key, JSONObject jobj, String defaultstring) {
		if(!jobj.has(key))
			return defaultstring;
		else
			return jobj.getString(key);
	}

	@PostMapping("/datasource")
	public String datasource(@RequestBody String body) {
		DataSource ds = null;
		String type="", ip= "", user="", password="", dbname="", port="5433", endpoints="", poolsize="", geo="";

		JSONObject jobj= new JSONObject(body);

		type= get("type", jobj, "postgres");
		ip = get("ip", jobj, "127.0.0.1");
		user = get("user", jobj, "yugabyte");
		password = get("password", jobj, "yugabyte");
		dbname = get("dbname", jobj, "testdb");
		port = get("port", jobj, "5433");
		endpoints = get("endpoints", jobj, "");
		poolsize= get("poolsize", jobj, "16");
		geo= get("geo", jobj, "");

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
