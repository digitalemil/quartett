let express = require('express');
let router = express.Router();
let app = express();
let request = require('request');
let pg = require('pg');

let pool = null;
let pgclient = null;

function get(key, jobj, defaultvalue) {
  if (key in jobj) {
    return jobj[key];
  }
  else
    return defaultvalue;
};

router.post('/datasource', function (req, res, next) {
  let jobj = JSON.parse(req.body);

  let datasource = {
    host: get("ip", jobj, "127.0.0.1"),
    database: get("dbname", jobj, "testdb"),
    port: get("port", jobj, "5433"),
    user: get("user", jobj, "yugabyte"),
    password: get("password", jobj, "yugabyte"),
    ssl: { rejectUnauthorized: false }
  };

  if (pool != null) {
    pool.end();
  }
  pool = new pg.Pool(datasource);
  pool.connect(function (err, c, done) {
    if (err) {
      console.error('Could not connect to the db', err);
      console.log("Trying without ssl.");
      datasource.sslmode= "disable"
      delete datasource.ssl;
      pool = new pg.Pool(datasource);
      pool.connect(function (err, c, done) {
        if (err) {
          console.error('Could not connect to the db', err);
          console.log("Giving up");
        }
        else {
          pgclient = c;
        }
      });
    }
    else {
      pgclient = c;
    }
  });
  let result= JSON.stringify(pool);
  console.log("Datasource: "+result);
  res.send(result);
  res.statusCode = 200;
  res.end();
});



router.post('/execute', async function (req, res, next) {
  let sql = "";
  let jobj = JSON.parse(req.body);

  sql = jobj.sql;
  try {
    const result = await pgclient.query(sql);
    console.log("Executing: " +sql);
    res.send(JSON.stringify(result.rows))
  }
  catch(e) {
    console.log(e);
    res.send(e);
  }
  
  res.statusCode = 200;
  res.end();
});

router.get('/home', function (req, res, next) {
  let n= "Quartet";
  if(req.query.name!= undefined) {
    n= req.query.name;
  }

  res.render("home", { name:n});
});

module.exports = router;

