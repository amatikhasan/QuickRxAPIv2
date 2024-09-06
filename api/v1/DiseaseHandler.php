<?php

require_once dirname(__FILE__) . '/MySqliStmt.php';

class DiseaseHandler
{

    private $con;

    public function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');

        $db = new DbConnect();
        $this->con = $db->connect();

    }

    public function createDisease($disease_data)
    {
        $name = $disease_data['name'];
        $cat_id = $disease_data['cat_id'];
        $clue_to_dx = $disease_data['clue_to_dx'];
        $advice = $disease_data['advice'];
        $treatment = $disease_data['treatment'];

        $now = new DateTime();
        $created_at = $now->format("d M, Y  h:i A");
        $updated_at = $created_at;

        $stmt = $this->con->prepare("INSERT INTO disease (name,cat_id,clue_to_dx,advice,treatment,created_at,updated_at) VALUES (?,?,?,?,?,?,?) ");
        $stmt->bind_param("sisssss", $name, $cat_id, $clue_to_dx, $advice, $treatment, $created_at, $updated_at);

        if ($stmt->execute()) {
            $id = $stmt->insert_id;
            return $id;
        }

        return "error";
    }

    public function createArticle($disease_data)
    {
        $name = $disease_data['name'];
        $cat_id = $disease_data['cat_id'];
        $details = $disease_data['details'];

        $now = new DateTime();
        $created_at = $now->format("d M, Y  h:i A");
        $updated_at = $created_at;

        $stmt = $this->con->prepare("INSERT INTO disease (name,cat_id,details,created_at,updated_at) VALUES (?,?,?,?,?) ");
        $stmt->bind_param("sisss", $name, $cat_id, $details, $created_at, $updated_at);

        if ($stmt->execute()) {
            $id = $stmt->insert_id;
            return $id;
        }

        return "error";
    }

    public function createDiseaseImage($disease_id, $file, $extension)
    {

        $disease_image = round(microtime(true) * 1000) . '.' . $extension;
        $fileLocation = dirname(__FILE__) . IMAGES_UPLOAD_PATH . $disease_image;

        move_uploaded_file($file, $fileLocation);

        $image_url = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]"."/api/v1/uploads/images/".$disease_image;

        $now = new DateTime();
        $created_at = $now->format("d M, Y  h:i A");
        $updated_at = $created_at;

        $stmt = $this->con->prepare("INSERT INTO disease_image (disease_id,image_url,created_at,updated_at) VALUES (?,?,?,?) ");
        $stmt->bind_param("isss", $disease_id, $image_url, $created_at, $updated_at);

        if ($stmt->execute()) {
            $id = $stmt->insert_id;
            return $id;
        }

        return "error";
    }

    public function getAllDisease()
    {

        $stmt = $this->con->prepare("SELECT * FROM disease ORDER BY name DESC");
        $stmt->execute();
        //$result = $stmt -> get_result();

        $diseases = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result->fetch_assoc()) {

            if (sizeof($row) > 0) {
                array_push($diseases, $row);
            }
        }
        $stmt->close();

        return $diseases;
    }

    public function getAllDiseaseImage($disease_id)
    {

        $stmt = $this->con->prepare("SELECT * FROM disease_image WHERE disease_id=? ORDER BY id DESC");
        $stmt->bind_param("i", $disease_id);
        $stmt->execute();
        //$result = $stmt -> get_result();

        $diseases = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result->fetch_assoc()) {

            if (sizeof($row) > 0) {
                array_push($diseases, $row);
            }
        }
        $stmt->close();

        return $diseases;
    }


    public function getDiseaseDetails($id)
    {

        $stmt = $this->con->prepare("SELECT * FROM disease WHERE id=?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        //$result = $stmt -> get_result();

        $disease = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result->fetch_assoc()) {
            $disease = $row;
        }
        $stmt->close();


        return $disease;
    }

    public function getDiseasesByCat($cat_id)
    {

        $stmt = $this->con->prepare("SELECT * FROM disease WHERE cat_id=? ORDER BY name ASC");
        $stmt->bind_param("i", $cat_id);
        $stmt->execute();
        //$result = $stmt -> get_result();

        $diseases = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result->fetch_assoc()) {
            if (sizeof($row) > 0) {
                array_push($diseases, $row);
            }
        }
        $stmt->close();


        return $diseases;
    }

    public function updateDisease($disease_data)
    {

        $id = $disease_data['id'];
        $name = $disease_data['name'];
        $cat_id = $disease_data['cat_id'];
        $clue_to_dx = $disease_data['clue_to_dx'];
        $advice = $disease_data['advice'];
        $treatment = $disease_data['treatment'];

        $now = new DateTime();
        $updated_at = $now->format("d M, Y  h:i A");

        $stmt = $this->con->prepare("UPDATE disease SET name=?,cat_id=?,clue_to_dx=?,advice=?,treatment=?,updated_at=? WHERE id=?");

        $stmt->bind_param("sissssi", $name, $cat_id, $clue_to_dx, $advice, $treatment, $updated_at, $id);

        if ($stmt->execute()) {
            return "Disease Updated Successfully!";
        }

        return "error";
    }

    public function updateArticle($disease_data)
    {

        $id = $disease_data['id'];
        $name = $disease_data['name'];
        $cat_id = $disease_data['cat_id'];
        $details = $disease_data['details'];

        $now = new DateTime();
        $updated_at = $now->format("d M, Y  h:i A");

        $stmt = $this->con->prepare("UPDATE disease SET name=?,cat_id=?,details=?,updated_at=? WHERE id=?");

        $stmt->bind_param("sissi", $name, $cat_id, $details, $updated_at, $id);

        if ($stmt->execute()) {
            return "Updated Successfully!";
        }

        return "error";
    }


    public function deleteDisease($id)
    {

        $stmt = $this->con->prepare("DELETE FROM disease WHERE id='$id'");

        if ($stmt->execute()) {
            return "deleted";
        } else {
            return "error";
        }
    }

    public function deleteDiseaseImage($id)
    {

        $stmt = $this->con->prepare("DELETE FROM disease_image WHERE id='$id'");

        if ($stmt->execute()) {
            return "deleted";
        } else {
            return "error";
        }
    }
}

?>