const express = require("express");
const bodyParser = require("body-parser");
const multer = require("multer");
const jwt = require("jsonwebtoken");

const decrypt = require("./decryptToken.js");
const key = require("./tokenKey.js");
const con = require("./databaseConnection.js");
const currrentDateAndHour = require("./currentDate.js");
const { token } = require("morgan");
const fs = require("fs");
const http = require("http");
const cors = require("cors");
const { ifError } = require("assert");

const privateKey = fs.readFileSync("server-key.pem", "utf8");
const certificate = fs.readFileSync("server-cert.pem", "utf8");

const credentials = { key: privateKey, cert: certificate };

const app = express();
const port = 2000;
const httpsServer = http.createServer(credentials, app);

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(cors());





//update prospector profile
app.post("/modification-prospector",(req,res) =>{
  const data = req.body;

  let UpdateProspector = () => {

let queryUpdateProspector = `UPDATE prospecteur set nomUtilisateur = ? , motDePasse = ?, mail = ? , phone = ? , nom = ?, prenom = ? WHERE idProspecteur = ?;`;

let idProspecteur = data.id;
let nomUtilisateur = data.username;
let motDePasse = data.password;
let mail = data.mail;
let phone = data.phone;
let nom = data.nom;
let prenom = data.prenom;

con.query(queryUpdateProspector,[nomUtilisateur,motDePasse,mail,phone,nom,prenom,idProspecteur],(err,rows) => {
  if (err) {
    console.log(err);
  }
  else {
     res.sendStatus(200);
  }
});
};

let verifyUser = (data) => {
  return new Promise((resolve, reject) => {
    let query = `SELECT * FROM prospecteur WHERE ((nomUtilisateur = ? or phone = ? or mail = ?) AND idProspecteur <> ?)`;

    let id = data.id;
    let username = data.username;
    let phoneNumber = data.phone;
    let email = data.mail;

    con.query(query, [username, phoneNumber, email,id], (err, results) => {
      if (err) {
        reject(err);
      } else {
        if (results.length > 0) {
          resolve(true);
        } else {
          resolve(false);
        }
      }
    });
  });
};
verifyUser(data)
  .then((userExists) => {
    if (!userExists) {
      UpdateProspector();
    } else {
      res.sendStatus(409);
    }
  })
  .catch((err) => {
    console.log(err);
    res.sendStatus(500);
  });
});
//delete prospecteur
app.post("/suppression-prospector",(req,res) => {
  const data = req.body;

  let DeleteProspector = () => {

    let queryDeleteProspector = `DELETE FROM prospecteur WHERE idProspecteur = ?`;
   
    let id = data.id;
    con.query(queryDeleteProspector,[id],(error,results) => {
      if(error){

      }
      else{
        res.sendStatus(200);
      }
    })

  };

  let verifyUser = (data) => {
    return new Promise((resolve, reject) => {
      let query = `SELECT * FROM prospecteur WHERE idProspecteur = ?`;
  
      let id = data.id;
     
      con.query(query, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          if (results.length < 0) {
            resolve(true);
          } else {
            resolve(false);
          }
        }
      });
    });
  };
  verifyUser(data)
    .then((userExists) => {
      if (!userExists) {
        DeleteProspector();
      } else {
        res.sendStatus(409);
      }
    })
    .catch((err) => {
      console.log(err);
      res.sendStatus(500);
    });
  





});


//project data
app.post("/get-data", (req, res) => {
    const nom = req.body['param-1'];
    const nbheures = req.body['param-2'];
    const taux = req.body['param-3'];
  
    let AddEns = () => {

    let queryAddEns = `INSERT INTO enseignant 
    (nom,nbheures,taux) VALUES (?,?,?);`;


con.query(queryAddEns,[nom,nbheures,taux],(err,rows) => {
  if (err) {
    console.log(err);
  }
  else {
     res.sendStatus(200);
  }

});
    };

let verifyUser = () => {
  return new Promise((resolve, reject) => {
    let query = `SELECT * FROM enseignant WHERE ((nom = ? or nbheures = ? or taux = ?))`;

    con.query(query, [nom, nbheures, taux], (err, results) => {
      if (err) {
        reject(err);
      } else {
        if (results.length > 0) {
          resolve(true);
        } else {
          resolve(false);
        }
      }
    });
  });
};
verifyUser()
  .then((userExists) => {
    if (!userExists) {
      AddEns();
    } else {
      res.sendStatus(409);
    }
  })
  .catch((err) => {
    console.log(err);
    res.sendStatus(500);
  });


});
app.post("/update-data", (req, res) => {
    const nom = req.body['param-1'];
    const nbheures = req.body['param-2'];
    const taux = req.body['param-3'];
    const id = req.body['param-4'];

    let queryUpdateProspector = `UPDATE enseignant set nom = ? , nbheures = ?, taux = ? WHERE id = ?;`;

    con.query(queryUpdateProspector,[nom,nbheures,taux,id],(err,rows) => {
      if (err) {
        console.log(err);
      }
      else {
         res.sendStatus(200);
      }
    
    });

});
app.post("/delete-data", (req, res) => {
  
  const id = req.body['param-1'];

  let queryUpdateProspector = `DELETE from enseignant WHERE id = ?;`;

  con.query(queryUpdateProspector,[id],(err,rows) => {
    if (err) {
      console.log(err);
    }
    else {
       res.sendStatus(200);
    }
  
  });

});



app.get("/table-data",(req,res) =>{
let queryTableData="SELECT * from enseignant";

con.query(queryTableData,(err, results) => {

  if (err) {
    console.error('Erreur de requete :', err);
    res.status(500).json({ error: 'erreur de requete SQL' })
  }
  else {
    const enseignant = results.map((profils) => ({
      id: profils.id,
      nom: profils.nom,
      nbheures: profils.nbheures,
      taux: profils.taux,
      salaire: (profils.nbheures * profils.taux)
    }));
    res.json(enseignant);
  }

});
});


app.get("/salary-data",(req,res) =>{

  // Requête SQL pour récupérer les taux et les heures de tous les enseignants
  const sqlQuery = 'SELECT taux, nbheures FROM enseignant';

  // Exécuter la requête SQL
  con.query(sqlQuery, (err, results) => {
      if (err) {
          console.error('Erreur lors de l\'exécution de la requête SQL : ', err);
          return res.status(500).json({ error: 'Erreur lors de la récupération des données de salaire' });
      }

      // Calculer les statistiques
      let totalSalaire = 0;
      let salaireMaximal = Number.MIN_VALUE;
      let salaireMinimal = Number.MAX_VALUE;

      results.forEach(enseignant => {
          const salaire = enseignant.taux * enseignant.nbheures;
          totalSalaire += salaire;
          salaireMaximal = Math.max(salaireMaximal, salaire);
          salaireMinimal = Math.min(salaireMinimal, salaire);
      });

      // Renvoyer les résultats au format JSON
      res.json({
          totalSalaire,
          salaireMaximal,
          salaireMinimal
      });
  });
});



//print the listening port :3000
httpsServer.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
