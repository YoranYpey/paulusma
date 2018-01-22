<?php
session_start();

$username = $_POST['username'];
$password = $_POST['password'];

//user logout
if(isset($_GET['logout']))
{
	unset($_SESSION['login']);
}


//login

if (isset($_SESSION['login']) &#038;& $_SESSION['login'] == $hash) {

	?>
 
		<p>Hello <?php echo $username; ?>, you have successfully logged in!</p>
		<a href="?logout=true">Logout?</a>
 
	<?php
}






if ($username =='admin' AND $password=='admin'){
	echo "You have logged in!";
}
else {
	echo "You have not logged in, username or password is incorrect!";
}
?>