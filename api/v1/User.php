<?php

//header('Content-Type: application/json');
require_once dirname(__FILE__) . '/UserHandler.php';
require_once dirname(__FILE__) . '/TokenHandler.php';

$response = array();

//for receiving object as request
$data = file_get_contents('php://input');
$user_data = json_decode($data, true);

if (isset($_GET['call'])) {
    switch ($_GET['call']) {

        case 'checkUser':

            if (isset($_POST['email']) && isset($_POST['phone'])) {
                $phone = $_POST['phone'];
                $email = $_POST['email'];

                $db = new UserHandler();
                $result = $db->isUserAvailable($email, $phone);

                $response['error'] = false;
                $response['response'] = $result;

            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'checkAccountWithPhoneReturnsId':

            if (isset($_POST['phone'])) {
                $phone = $_POST['phone'];

                $db = new UserHandler();
                $result = $db->isAccountRegistered($phone);

                if ($result != null) {
                    $response['error'] = false;
                    $response['response'] = $result;
                } else {
                    $response['error'] = true;
                    $response['response'] = 0;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'login':

            if (isset($_POST['email_or_phone']) && isset($_POST['password'])) {
                $emailOrPhone = $_POST['email_or_phone'];
                $password = $_POST['password'];

                $db = new UserHandler();
                $result = $db->login($emailOrPhone, $password);

                $response['error'] = false;
                $response['response'] = $result;

            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'loginReturnUserDetails':

            if (isset($_POST['email_or_phone']) && isset($_POST['password'])) {
                $emailOrPhone = $_POST['email_or_phone'];
                $password = $_POST['password'];

                $db = new UserHandler();
                $result = $db->loginReturnUserDetails($emailOrPhone, $password);

                $response = $result;

            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'loginReturnsToken':

            if (isset($_POST['email_or_phone']) && isset($_POST['password'])) {
                $emailOrPhone = $_POST['email_or_phone'];
                $password = $_POST['password'];

                $db = new UserHandler();
                $result = $db->loginReturnsToken($emailOrPhone, $password);

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

        case 'refreshToken':

            if (isset($_POST['email_or_phone']) && isset($_POST['password'])) {
                $emailOrPhone = $_POST['email_or_phone'];
                $password = $_POST['password'];

                $db = new UserHandler();
                $result = $db->refreshToken($emailOrPhone, $password);

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

        case 'register':

            if ($_SERVER['REQUEST_METHOD'] == "POST") {

                $name = $_POST['name'];
                $phone = $_POST['phone'];
                $firebase_uid = $_POST['firebase_uid'];
                $unique_id = $_POST['unique_id'];
                $email = $_POST['email'];
                $password = $_POST['password'];
                $dob = $_POST['dob'];
                $reg_number = $_POST['reg_number'];
                $account_status = $_POST['account_status'];

                $user_data = array("name" => $name, "phone" => $phone, "firebase_uid" => $firebase_uid, "unique_id" => $unique_id, "email" => $email, "password" => $password, "dob" => $dob, "reg_number" => $reg_number, "account_status" => $account_status);

                $save = new UserHandler();
                $result = $save->createUser($user_data);

                if ($result != "error") {
                    $response['error'] = false;
                    $response['response'] = $result;
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Required fields are missing!!';
                }


            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'registerWithImage':

            if (isset($_POST['name']) && $_FILES['file']['error'] === UPLOAD_ERR_OK) {
                $db = new UserHandler();

                $file = $_FILES['file']['tmp_name'];

                $name = $_POST['name'];
                $phone = $_POST['phone'];
                $email = $_POST['email'];
                $password = $_POST['password'];
                $firebase_uid = $_POST['firebase_uid'];
                $unique_id = $_POST['unique_id'];
                $dob = $_POST['dob'];
                $reg_number = $_POST['reg_number'];
                $account_status = $_POST['account_status'];

                $fileExt = getFileExtension($_FILES['file']['name']);

                $user_data = array("name" => $name, "phone" => $phone, "email" => $email, "password" => $password, "firebase_uid" => $firebase_uid, "unique_id" => $unique_id, "dob" => $dob, "reg_number" => $reg_number, "account_status" => $account_status);
                $result = $db->createUserWithImage($file, $fileExt, $user_data);


                if ($result != "error") {
                    $response['error'] = false;
                    $response['response'] = $result;
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Required fields are missing!!';
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'get':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if (isset($_POST['email_or_phone'])) {
                        $emailOrPhone = $_POST['email_or_phone'];
                        $db = new UserHandler();
                        $result = $db->getUserDetails($emailOrPhone);

                        $response = $result;
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

        case 'getAllUsers':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    $db = new UserHandler();
                    $result = $db->getAllUsers();

                    $response = $result;
                } else {
                    http_response_code(401);
                }
            } else {
                http_response_code(401);
            }
            break;

        case 'getByName':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if (isset($_POST['name'])) {
                        $name = $_POST['name'];
                        $db = new UserHandler();
                        $result = $db->getUserByName($name);

                        $response = $result;
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

        case 'update':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if ($_SERVER['REQUEST_METHOD'] == "POST") {

                        $id = $_POST['id'];
                        $name = $_POST['name'];
                        $phone = $_POST['phone'];
                        $email = $_POST['email'];
                        $password = $_POST['password'];
                        $dob = $_POST['dob'];
                        $reg_number = $_POST['reg_number'];

                        $user_data = array("id" => $id, "name" => $name, "phone" => $phone, "email" => $email, "password" => $password, "dob" => $dob, "reg_number" => $reg_number);


                        $save = new UserHandler();
                        $result = $save->updateUser($user_data);

                        if ($result == "updated") {
                            $response['error'] = false;
                            $response['response'] = 'Updated Successfully!';
                        } else {
                            $response['error'] = true;
                            $response['response'] = 'Something went wrong!';
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

        case 'updateUniqueId':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if ($_SERVER['REQUEST_METHOD'] == "POST") {

                        $id = $_POST['id'];
                        $unique_id = $_POST['unique_id'];


                        $save = new UserHandler();
                        $result = $save->updateUniqueId($id, $unique_id);

                        if ($result == "updated") {
                            $response['error'] = false;
                            $response['response'] = 'Updated Successfully!';
                        } else {
                            $response['error'] = true;
                            $response['response'] = 'Something went wrong!';
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

        case 'updatePassword':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if ($_SERVER['REQUEST_METHOD'] == "POST") {

                        $id = $_POST['id'];
                        $password = $_POST['password'];


                        $save = new UserHandler();
                        $result = $save->updatePassword($id, $password);

                        if ($result == "updated") {
                            $response['error'] = false;
                            $response['response'] = 'Password changed Successfully!';
                        } else {
                            $response['error'] = true;
                            $response['response'] = 'Something went wrong! Please try again';
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

        case 'updateProfileImage':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if (isset($_POST['id']) && $_FILES['file']['error'] === UPLOAD_ERR_OK) {
                        $db = new UserHandler();

                        $id = $_POST['id'];
                        $file = $_FILES['file']['tmp_name'];

                        $fileExt = getFileExtension($_FILES['file']['name']);

                        $result = $db->updateProfileImage($id, $file, $fileExt);


                        if ($result == "updated") {
                            $response['error'] = false;
                            $response['response'] = 'Updated Successfully!';
                        } else {
                            $response['error'] = true;
                            $response['response'] = 'Something went wrong!';
                        }


                    } else {
                        $id = $_POST['id'];
                        $file = $_FILES['file']['tmp_name'];
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

        case 'updateAccountStatus':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if ($_SERVER['REQUEST_METHOD'] == "POST") {

                        $id = $_POST['id'];
                        $account_status = $_POST['account_status'];
                        $account_valid_from = $_POST['account_valid_from'];
                        $account_valid_until = $_POST['account_valid_until'];

                        $save = new UserHandler();
                        $result = $save->updateAccountStatus($id, $account_status, $account_valid_from, $account_valid_until);

                        if ($result == "updated") {
                            $response['error'] = false;
                            $response['response'] = 'Updated Successfully!';
                        } else {
                            $response['error'] = true;
                            $response['response'] = 'Something went wrong!';
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

        case 'updateAccountStatusValue':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if ($_SERVER['REQUEST_METHOD'] == "POST") {

                        $id = $_POST['id'];
                        $account_status = $_POST['account_status'];

                        $save = new UserHandler();
                        $result = $save->updateAccountStatusValue($id, $account_status);

                        if ($result == "updated") {
                            $response['error'] = false;
                            $response['response'] = 'Updated Successfully!';
                        } else {
                            $response['error'] = true;
                            $response['response'] = 'Something went wrong!';
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

function getFileExtension($file){
    $path_parts = pathinfo($file);
    return $path_parts['extension'];
}

echo json_encode($response);
