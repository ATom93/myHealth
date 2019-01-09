<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$U_username = urldecode($_POST['username']);


// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT DM.Model
FROM daily_model as DM inner join user as U on DM.ID_User=U.ID_User
WHERE U.Username='".$U_username."'
ORDER BY DM.Timestamp DESC
LIMIT 1
";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		$model = $row['Model'];
	}
	echo $model;
} else {  
    echo "0 results";
}
$conn->close();
?>
