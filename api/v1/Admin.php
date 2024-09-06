<?php

 
require_once dirname(__FILE__) . '/AdminHandler.php';
 
$response = array();

//for receiving object as request
$data = file_get_contents('php://input');
$info_data = json_decode($data , true);

if (isset($_GET['call'])) {
    switch ($_GET['call']) {
            
         case 'login':
 
            if (isset($_POST['username']) && isset($_POST['password'])) {
                 
                $save = new AdminHandler();
                $result=$save->login($_POST['username'], $_POST['password']);
                
               if ($result!=null) {
                    $response['error'] = false;
                    $response['response'] = "$result";
                }else {
                    $response['error'] = true;
                    $response['response'] = "username or password is wrong!";
                }
 
            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }
 
            break;
            
            case 'updatePassword':

            if ($_SERVER['REQUEST_METHOD'] == "POST") {

                $id = $_POST['id'];
                $password = $_POST['password'];


                $save = new AdminHandler();
                $result = $save->updatePassword($id, $password);

                if ($result == "updated") {
                    $response['error'] = false;
                    $response['response'] = 'Password changed Successfully!';
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Old Password is wrong!';
                }


            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;
            
    }
}

if (isset($_GET['get'])) {
    switch ($_GET['get']) {
            
        case 'info':
            
            $db = new ApplicationHandler();
            $result=$db->getAppInfo();
            
            $response=$result;
            
            break;
            
    }
}
 
echo json_encode($response);
 
function getFileExtension($file)
{
    $path_parts = pathinfo($file);
    return $path_parts['extension'];
}

?>