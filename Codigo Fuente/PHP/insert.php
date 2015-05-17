<?php
$host = "localhost";
$uname = "root";
$password = "";
$db = "test";

$con = mysqli_connect ( $host, $uname, $password , $db) or die ( "Connection Failed" );

//mysql_select_db ( $db, $con ) or die ( "Database Selection Failed" );

$uId = $_REQUEST ['id'];
$fname = $_REQUEST ['fName'];
$lname = $_REQUEST ['lName'];
$lat = $_REQUEST['lat'] ;
$lon = $_REQUEST['lon'];

$id = isset($_POST['id']) ? $_POST['id'] : "";
$fname = isset($_POST['fName']) ? $_POST['fName'] : "";
$lname = isset($_POST['lName']) ? $_POST['lName'] : "";
$lat = isset($_POST['lat']) ? $_POST['lat'] : "";
$lon = isset($_POST['lon']) ? $_POST['lon'] : "";

$flag ['code'] = 0;

if ($result = mysqli_query (  $con ,"INSERT INTO gps VALUES('$fname', '$lname', '$id' ,'$lat' , '$lon') ")) {
    $flag ['code'] = 1;
    echo "hi";
}

echo (json_encode ( $flag )) ;
mysqli_close ( $con );

?>