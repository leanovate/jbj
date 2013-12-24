<?php
	require_once("container.php");
	// TODO remove config.php dependency
	/**
	* Feature story class.
	*
	* @author		Alexander Palmo <apalmo at bigevilbrain dot com>
	* @access		public
	*/
	class Group extends container {
		var $path, $html_template, $tags;
		
		/**
		* Contructor
		*
		* @param		string $filename
		* @return		null
		*/
		function Group($filename='',$is_cached=false) {
			$this->path = GROUP_DIR;
			$this->html_template = TEMPLATE_DIR.'data/group.tpl';
		
			$this->filename = $filename;
			$this->is_cached_data = $is_cached;
			
			$this->setDefaults();
		}
		
		function setDefaults() {
			// Allowed Tags
			$this->tags = array();
			array_push($this->tags, 'ID');
			array_push($this->tags, 'GROUP_NAME');
			array_push($this->tags, 'SUPERUSER');
			
			array_push($this->tags, 'COMMENTS'); // Stores the drop-down menu value. Not actually used for permissions.
			array_push($this->tags, 'COMMENT_VIEW');
			array_push($this->tags, 'COMMENT_POST_WITH_APPROVAL');
			array_push($this->tags, 'COMMENT_POST');
			array_push($this->tags, 'COMMENT_MODERATE');
			array_push($this->tags, 'COMMENT_EDIT');
			array_push($this->tags, 'COMMENT_DELETE');
			
			array_push($this->tags, 'ENTRIES'); // Stores the drop-down menu value. Not actually used for permissions.
			array_push($this->tags, 'ENTRY_VIEW');
			array_push($this->tags, 'ENTRY_POST_WITH_APPROVAL');
			array_push($this->tags, 'ENTRY_POST');
			array_push($this->tags, 'ENTRY_MODERATE');
			array_push($this->tags, 'ENTRY_EDIT');
			array_push($this->tags, 'ENTRY_DELETE');
			
			
			// Cache Excluded Tags
			$this->do_not_cache_tags = array();
			// array_push($this->do_not_cache_tags, 'EXAMPLE');
			
			// Default Values
			foreach ($this->tags as $key) {
				$this->setTag($key, '');
			}

			$this->setTag('SUPERUSER', '0');
			
			$this->set_comment_permissions_by_name();
			$this->set_entry_permissions_by_name();
		}
		
		function write_file() {
			delete_groups_cache();
			$result = parent::write_file();
			return $result;
		}
		
		function set_comment_permissions_by_name($val='no_access') {
			// Dropdown value
			$this->setTag('COMMENTS', $val);
			
			// Zero out all permissions
			$this->setTag('COMMENT_VIEW', 				'0');
			$this->setTag('COMMENT_POST_WITH_APPROVAL', '0');
			$this->setTag('COMMENT_POST', 				'0');
			$this->setTag('COMMENT_MODERATE', 			'0');
			$this->setTag('COMMENT_EDIT', 				'0');
			$this->setTag('COMMENT_DELETE', 			'0');
		
			// Permissions cascade. If you have permission to delete, then you get all the permissions.
			switch ($val) {
				case 'delete':
					$this->setTag('COMMENT_DELETE', 			'1');
				case 'edit':
					$this->setTag('COMMENT_EDIT', 				'1');
				case 'moderate':
					$this->setTag('COMMENT_MODERATE', 			'1');
				case 'post':
					$this->setTag('COMMENT_POST', 				'1');
				case 'post_with_approval':
					$this->setTag('COMMENT_POST_WITH_APPROVAL', '1');
				case 'view':
					$this->setTag('COMMENT_VIEW', 				'1');
				case 'no_access':
					// Nothing!
					break;
			}
		}
		
		function set_entry_permissions_by_name($val='no_access') {
			// Dropdown value
			$this->setTag('ENTRIES', $val);
			
			// Zero out all permissions
			$this->setTag('ENTRY_VIEW', 				'0');
			$this->setTag('ENTRY_POST_WITH_APPROVAL', 	'0');
			$this->setTag('ENTRY_POST', 				'0');
			$this->setTag('ENTRY_MODERATE', 			'0');
			$this->setTag('ENTRY_EDIT', 				'0');
			$this->setTag('ENTRY_DELETE', 				'0');
		
			// Permissions cascade. If you have permission to delete, then you get all the permissions.
			switch ($val) {
				case 'delete':
					$this->setTag('ENTRY_DELETE', 				'1');
				case 'edit':
					$this->setTag('ENTRY_EDIT', 				'1');
				case 'moderate':
					$this->setTag('ENTRY_MODERATE', 			'1');
				case 'post':
					$this->setTag('ENTRY_POST', 				'1');
				case 'post_with_approval':
					$this->setTag('ENTRY_POST_WITH_APPROVAL', 	'1');
				case 'view':
					$this->setTag('ENTRY_VIEW', 				'1');
				case 'no_access':
					// Nothing!
					break;
			}
		}
			
	}
	
	/* -------------------- SORT -------------------- */
	
	/* -------------------- CACHE -------------------- */
	
	function build_groups_cache() {
		$filearr = fileio::file_listing(GROUP_DIR, array('.txt'));
		
		$arr = array();
		for ($i=0; $i<count($filearr); $i++) {
			$filename = $filearr[$i];
			
			$record = new Group($filename);
			$record->read_file();
			
			array_push($arr, $record->getCacheData());
		}
		
		if (count($arr)) {
			$str = arrays::implode_list($arr);
			$result = fileio::write_file(CACHE_DIR.'groups.txt', $str);
			return ($result!==false);
		} else {
			return false;
		}
	}
	
	function get_groups_cache() {
		if (!file_exists(CACHE_DIR.'groups.txt')) {
			build_groups_cache();
		}
		$str = fileio::read_file(CACHE_DIR.'groups.txt', true);
		
		$record_arr = array();
		if ($str != null) {
			$arr = arrays::explode_list($str);
			$record_arr = array();
			for ($i=0; $i<count($arr); $i++) {
				$data = $arr[$i];
				
				$record = new Group($data['ID'].'.txt', true);
				$record->data = $data;
				
				array_push($record_arr, $record);
			}
			
			sort_by_arbitrary_tag($record_arr, 'GROUP_NAME', false);
		}
		
		return($record_arr);
	}
	
	function delete_groups_cache() {
		fileio::delete_file(CACHE_DIR.'groups.txt');
	}
	
	/* -------------------- SEARCH -------------------- */
	
	function find_group_by_id($group_id='') {
		$record_arr = get_groups_cache();
		
		for ($i=0; $i<count($record_arr); $i++) {
			$record = $record_arr[$i];
			
			$id = $record->getTag('ID');
			if ($group_id == $id) {
				return $record;
			}
			unset($record);
		}	
	}
	
	function find_group_by_group_name($group_name='') {
		$record_arr = get_groups_cache();
		
		for ($i=0; $i<count($record_arr); $i++) {
			$record = $record_arr[$i];
			
			$name = $record->getTag('GROUP_NAME');
			if ($group_name == $name) {
				return $record;
			}
			unset($record);
		}	
	}
	
	function groups_to_drop_down($sel=null) {
		$record_arr = get_groups_cache();
		
		$arr = array();
		for ($i=0; $i<count($record_arr); $i++) {
			$record = $record_arr[$i];
			
			$key = $record->getTag('ID');
			$val = $record->getTag('GROUP_NAME');
			
			$arr[$key] = $val;
			unset($record);
		}
		
		return html::array_to_dropdown($arr, $sel);
	}
	
?>
