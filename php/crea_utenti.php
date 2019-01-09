<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$U_name = urldecode($_POST['name']);
$U_surname = urldecode($_POST['surname']);
$U_sex = urldecode($_POST['sex']);
$U_birthdate = urldecode($_POST['birthdate']);

$U_birthdate = strtotime($U_birthdate);
$U_birthdate = date('Y-m-d',$U_birthdate);

$U_altezza = urldecode($_POST['altezza']);
$U_username = urldecode($_POST['username']);
$U_password = urldecode($_POST['password']);




// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
$sql = "SELECT Username FROM user WHERE Username='".$U_username."'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    echo "0"; //L'utente esiste già
} else {
    
    $sql = "INSERT INTO user (Name, Surname, Sex, Birth_Date, Altezza, Username, Password) VALUES ('".$U_name."', '".$U_surname."', '".$U_sex."','".$U_birthdate."','".$U_altezza."','".$U_username."','".$U_password."')";
    
    if ($conn->query($sql) === TRUE) {   
        echo "1"; //Utente creato con successo
    } else {
        echo "2"; //Errore
    }
            
}

$conn->close();
?>

