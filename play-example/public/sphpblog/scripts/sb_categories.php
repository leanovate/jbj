<?php 
	require_once("config.php");
	require_once("classes/fileio.php");

	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com
	
	// get_category_array ()
	// write_categories ( $catArray )
	// get_category_by_id ( $id )
	
	// ------------------
	// Category Functions
	// ------------------
	
	function get_category_array () {
		// Load category information from file
		//
		$contents = sb_read_file( CONFIG_DIR.'categories.txt' );
		
		$catArray = Array();
		if ( $contents ) {
			$temp_array = explode( '|', $contents );
			for ( $i=0; $i<count($temp_array); $i = $i + 3 ) {
				$id_number = $temp_array[$i];
				$name_str = $temp_array[$i+1];
				$space_count = $temp_array[$i+2];
				
				array_push( $catArray, Array( $id_number, $name_str, $space_count ) );
			}
		}
		
		return $catArray;
	}
	
	function get_sub_categories ( $parent_id ) {
		
		$result_arr = Array();
			
		if ( isset( $parent_id ) ) {
			$cat_arr = get_category_array();
			
			// Look for matching category id
			for ( $i=0; $i<count($cat_arr); $i++ ) {
				$id_number = $cat_arr[$i][0];
				// $name_str = $cat_arr[$i][1];
				$space_count = $cat_arr[$i][2];
				
				// Found it...
				if ($id_number==$parent_id) {
					$parent_space_count = $space_count;
					
					// Look for sub categories
					for ( $j=$i+1; $j<count($cat_arr); $j++ ) {
						$id_number = $cat_arr[$j][0];
						// $name_str = $cat_arr[$i][1];
						$space_count = $cat_arr[$j][2];
						
						if ( $space_count > $parent_space_count ) {
							array_push( $result_arr, $id_number );
						} else {
							break 2;
						}
					}
				}
			}
		}
		
		return( $result_arr );
	}
	
	function write_categories ( $catArray ) {
		// Save the category array
		//
		// Array( $id_number, $name_str, $space_count );
		$str = '';
		for ( $i = 0; $i < count( $catArray ); $i++ ) {
			$str  .= implode( '|', $catArray[$i] );
			if ( $i < count( $catArray ) - 1 ) {
				$str  .= '|';
			}
		}
		
		$ok = sb_write_file( CONFIG_DIR.'categories.txt', $str );
		
		if ( $ok ) {
			return true;
		} else {
			return false;
		}
	}
	
	function get_category_by_id ( $id ) {
		// Look up a Category by it's ID
		//
		$catArray = get_category_array();
		for ( $i = 0; $i < count( $catArray ); $i++ ) {
			if ( $catArray[ $i ][ 0 ] == $id ) {
				return $catArray[ $i ][ 1 ];
			}
		}
	}
?>
