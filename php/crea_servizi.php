<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$U_name = urldecode($_POST['name']);
$U_description = urldecode($_POST['description']);

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
$sql = "SELECT Name FROM service WHERE Name='".$U_name."'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    echo "0"; //L'utente esiste già
} else {
    
    $sql = "INSERT INTO service (Name, Description) VALUES ('".$U_name."','".$U_description."')";
    
    if ($conn->query($sql) === TRUE) {   
        echo "1"; //Utente creato con successo
    } else {
        echo "2"; //Errore
    }
            
}

$conn->close();
?>

