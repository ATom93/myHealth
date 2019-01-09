<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";

$U_username =  urldecode($_POST['username']);

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT * FROM (SELECT U.Username,S.Name,USG.ID_Usage,USG.Value,USG.Date,USG.Time FROM user as U inner join user_service as US on U.ID_User=US.ID_User inner join service as S on US.ID_Service=S.ID_Service inner join `usage`as USG on US.ID_User_Service=USG.ID_User_Service WHERE U.Username = '".$U_username."' and S.Name='peso' ORDER BY USG.Date DESC LIMIT 2) as tab ORDER BY tab.Date LIMIT 1";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		
		$date = $row["Date"];
	}
	
	$myJSON = json_encode($date);
	echo $date;
    
} else {  
    echo "0 results";
}
$conn->close();
?>

