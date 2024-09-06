<?php

require_once dirname(__FILE__) . '/MySqliStmt.php';

class CategoryHandler
{
 
    private $con;
 
    public function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');
 
        $db = new DbConnect();
        $this->con = $db->connect();
        
    }
 
    public function createCategoryWithFile($file, $extension,$category_data)
    {
        $name=$category_data['name'];
        $parent_cat_id=$category_data['parent_cat_id'];
        $type=$category_data['type'];

        $category_image = round(microtime(true) * 1000) . '.' . $extension;
        $filedest = dirname(__FILE__) . IMAGES_UPLOAD_PATH . $category_image;
        
        move_uploaded_file($file, $filedest);
 
        $image_url = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]"."/api/v1/uploads/images/".$category_image;
        
        
        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;
 
        $stmt = $this->con->prepare("INSERT INTO category (name,parent_cat_id,type,image_url,created_at,updated_at) VALUES (?,?,?,?,?,?) ");
        $stmt->bind_param("siisss",$name,$parent_cat_id,$type,$image_url,$created_at,$updated_at);
        
        if ($stmt->execute()){
            $id=$stmt->insert_id;
            $category_data=array("id"=>$id ,"name"=>$name,"parent_cat_id"=>$parent_cat_id ,"type"=>$type, "image_url"=>$image_url, "created_at"=>$created_at, "updated_at"=>$updated_at);
            
            return $category_data;
        }
            
        return "error";
    }

    public function createCategory($category_data)
    {
        $name=$category_data['name'];
        $parent_cat_id=$category_data['parent_cat_id'];
        $type=$category_data['type'];
        $image_bytes = $category_data['image_bytes'];

        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;

        $stmt = $this->con->prepare("INSERT INTO category (name,parent_cat_id,type,image_bytes,created_at,updated_at) VALUES (?,?,?,?,?,?) ");
        $stmt->bind_param("siisss",$name,$parent_cat_id,$type,$image_bytes,$created_at,$updated_at);

        if ($stmt->execute()){
            $id=$stmt->insert_id;
            $category_data=array("id"=>$id ,"name"=>$name,"parent_cat_id"=>$parent_cat_id ,"type"=>$type, "image_bytes"=>$image_bytes, "created_at"=>$created_at, "updated_at"=>$updated_at);

            return $category_data;
        }

        return "error";
    }

    public function getAllCategory()
    {

        $stmt = $this->con->prepare("SELECT * FROM category ORDER BY name ASC");
        $stmt->execute();
        //$result = $stmt -> get_result();

        $categories = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) {
            if(sizeof($row)>0){
                array_push($categories, $row);
            }
        }
        $stmt->close();

        return $categories;
    }

    public function getAllMainCategory()
    {
        
        $stmt = $this->con->prepare("SELECT * FROM category WHERE type=1 ORDER BY id DESC");
        $stmt->execute();
        //$result = $stmt -> get_result();
 
        $categories = array();
        
        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) { 
	    if(sizeof($row)>0){
	            array_push($categories, $row);
	        }
        }
        $stmt->close();
 
        return $categories;
    }

    public function getAllParentCategory($sub_cat_id)
    {

        $stmt = $this->con->prepare("SELECT * FROM category WHERE parent_cat_id=? ORDER BY name ASC");
        $stmt->bind_param("i", $parent_cat_id);
        $stmt->execute();
        //$result = $stmt -> get_result();

        $categories = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) {
            if(sizeof($row)>0){
                array_push($categories, $row);
            }
        }
        $stmt->close();

        return $categories;
    }

    public function getAllSubCategoryById($parent_cat_id)
    {
        
        $stmt = $this->con->prepare("SELECT * FROM category WHERE parent_cat_id=? ORDER BY name ASC");
        $stmt->bind_param("i", $parent_cat_id);
        $stmt->execute();
        //$result = $stmt -> get_result();
 
        $categories = array();
        
        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) { 
	    if(sizeof($row)>0){
	            array_push($categories, $row);
	        }
        }
        $stmt->close();
 
        return $categories;
    }
    
    
    public function getCategoryById($id)
    {
        
        $stmt = $this->con->prepare("SELECT * FROM category WHERE id=? ORDER BY id ASC");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        //$result = $stmt -> get_result();
 
        $categories = array();
        
        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) { 
	    if(sizeof($row)>0){
	            array_push($categories, $row);
	        }
        }
        $stmt->close();
 
        return $categories;
    }
    
    
   public function getAllSubCategory()
    {
        
        $stmt = $this->con->prepare("SELECT * FROM category WHERE type=2  ORDER BY name ASC");
        $stmt->execute();
 
        $categories = array();
        
        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) {
	    if(sizeof($row)>0){
	            array_push($categories, $row);
	        }
        }
        $stmt->close();
 
        return $categories;
    }
    
    
    public function updateCategoryWithFile($file, $extension,$category_data)
    {
        $category_image = round(microtime(true) * 1000) . '.' . $extension;
        $filedest = dirname(__FILE__) . IMAGES_UPLOAD_PATH . $category_image;
        
        move_uploaded_file($file, $filedest);
 
        $image_url = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]"."/api/v1/uploads/images/".$category_image;
        
     
        $id=$category_data['id'];
        $name=$category_data['name'];
        $parent_cat_id=$category_data['parent_cat_id'];
        $type=$category_data['type'];
        
        $old_image_url=$category_data['image_url'];
        $parts=explode("images/",$old_image_url);
        $image=$parts[1];
        unlink(dirname(__FILE__) . IMAGES_UPLOAD_PATH . $image);
        
        $now = new DateTime();
        $updated_at= $now->format("d M, Y h:i A");
 
        $stmt = $this->con->prepare("UPDATE category SET name=?,parent_cat_id=?,type=?,image_url=?,updated_at=? WHERE id=?");
        $stmt->bind_param("siissi",$name,$parent_cat_id, $type, $image_url, $updated_at, $id);
        
        if ($stmt->execute()){
            $category_data=array("id"=>$id ,"name"=>$name,"parent_cat_id"=>$parent_cat_id ,"type"=>$type, "image_url"=>$image_url, "updated_at"=>$updated_at);
            
            return $category_data;
        }
            
        return "error";
    }
    
    
    public function updateCategory($category_data)
    {
        
        $id=$category_data['id'];
        $name=$category_data['name'];
        $parent_cat_id=$category_data['parent_cat_id'];
        $type=$category_data['type'];
        $image_url=$category_data['image_url'];

        $now = new DateTime();
        $updated_at= $now->format("d M, Y h:i A");
 
        $stmt = $this->con->prepare("UPDATE category SET name=?,parent_cat_id=?,type=?,image_url=?,updated_at=? WHERE id=?");
        $stmt->bind_param("siissi",$name,$parent_cat_id, $type, $image_url, $updated_at, $id);
        
        if ($stmt->execute()){
            $category_data=array("id"=>$id ,"name"=>$name,"parent_cat_id"=>$parent_cat_id ,"type"=>$type, "image_url"=>$image_url, "updated_at"=>$updated_at);
            
            return $category_data;
        }
            
        return "error";
    }
    
    
    
    public function deleteCategory($id,$image_url)
    {
        $parts=explode("images/",$image_url);
        $image=$parts[1];
        
        $stmt = $this->con->prepare("DELETE FROM category WHERE id='$id'");
 
         if ($stmt->execute()) {

             unlink(dirname(__FILE__) . IMAGES_UPLOAD_PATH . $image);
            return "deleted";
        }
        else{
           
            return "error";
        }
    }
}

?>