<?php

require_once dirname(__FILE__) . '/AdminHandler.php';
require_once dirname(__FILE__) . '/TokenHandler.php';

$response = array();

//for receiving object as request
$data = file_get_contents('php://input');
$info_data = json_decode($data, true);

if (isset($_GET['call'])) {
    switch ($_GET['call']) {

        case 'login':

            if (isset($_POST['username']) && isset($_POST['password'])) {

                $save = new AdminHandler();
                $result = $save->login($_POST['username'], $_POST['password']);

                if ($result != "error") {
                    $response['error'] = false;
                    $response['response'] = $result;
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Error, Please Try Again!';
                }
            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'updatePassword':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;

            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if ($_SERVER['REQUEST_METHOD'] == "POST") {
                        $password = $_POST['password'];
                        $save = new AdminHandler();
                        $result = $save->updatePassword($password, $jwtToken);

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
                } else {
                    http_response_code(401);
                }
            } else {
                http_response_code(401);
            }

            break;
    }
}

echo json_encode($response);

function getFileExtension($file){
    $path_parts = pathinfo($file);
    return $path_parts['extension'];
}
