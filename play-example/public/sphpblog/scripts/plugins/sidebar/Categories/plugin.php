<?php
	/**
	* Categories widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Categories extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Categories () {
			$this->plugin = 'Categories';
			$this->loadPrefs();
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('menu_categories');
		}
		
		function getContent () {
			$str = '';
			
			$catArray = get_category_array();
			if ( count($catArray) > 0) {
				$blogEntriesArray = blog_entry_listing();
				$blogEntryValuesArray = array();
				$catHasEntryArray = array();
				
				for ($i = 0; $i < count($catArray); $i++) {
					$catHasEntryArray[$catArray[$i][0]] = 0;
				}
				
				if ( count($blogEntriesArray) > 0) {
					for ( $i = 0; $i < count( $blogEntriesArray ); $i++) {
						list( $_entry_filename, $_year_dir, $_month_dir ) = explode( '|', $blogEntriesArray[$i] );
						$blogEntryValue = blog_entry_to_array(CONTENT_DIR . $_year_dir . '/' . $_month_dir . '/' . $_entry_filename);
						
						if (strpos($blogEntryValue['CATEGORIES'], ',') === false) {
							if (isset($catHasEntryArray[$blogEntryValue['CATEGORIES']])) {
								$catHasEntryArray[$blogEntryValue['CATEGORIES']]++;
							} else {
								$catHasEntryArray[$blogEntryValue['CATEGORIES']] = 1;
							}
						} else {
							$entryCategories = explode(',', $blogEntryValue['CATEGORIES']);
							for ($y = 0; $y < count($entryCategories); $y++) {
								if (isset($catHasEntryArray[$entryCategories[$y]])) {
									$catHasEntryArray[$entryCategories[$y]]++;
								} else {
									$catHasEntryArray[$entryCategories[$y]] = 1;
								}
							}
						}
					}
				}

				for ( $i = 0; $i < count( $catArray ); $i++ ) {
					$id_number = $catArray[$i][0];
					$name_str = $catArray[$i][1] . " (" . $catHasEntryArray[$id_number] . ")";  //  <-- extended Category Name
					$space_count = $catArray[$i][2];
					for ( $j = 0; $j < $space_count; $j++ ) {
						// Indent the proper number of spaces...
						$str  .= '&nbsp;';
					}
					if ( !empty($GLOBALS[ 'category' ]) AND $GLOBALS[ 'category' ] == $id_number ) {
						// This is the current viewing category...
						$str  .= $name_str;
					} else {
						$str  .= "<a href=\"" . BASEURL . "index.php?category=" . $id_number . "\">" . $name_str . "</a>";
					}
					if ( $i == count( $catArray ) - 1 ) {
						// Last item...
						$str  .= "\n";
					} else {
						$str  .= "<br />\n";
					}
				}
			}
			
			return $str;
		}
	}
?>
