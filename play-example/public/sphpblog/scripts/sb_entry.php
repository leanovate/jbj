<?php
	// Simple PHP Blog is released under the GNU Public License.

	// blog_entry_to_array ( $entryFile )
	// write_entry ( $blog_subject, $blog_text, $tb_ping, $updateFile, $blog_categories, $blog_relatedlink, $blog_date=NULL )
	// implode_with_keys( $array, $separator = '|' )
	// explode_with_keys( $str, $separator = '|' )
	// delete_dir( $dir )
	// delete_entry( $entry, $path )
	// write_rating( $y, $m, $entry, $rating )
	// read_rating( $y, $m, $entry )
	// write_modifica ( $blog_subject, $blog_text, $tb_ping, $updateFile, $blog_categories, $blog_date=NULL, $filename)
	
	// --------------------
	// Blog Entry Functions
	// --------------------

	function blog_entry_to_array ( $entryFile ) {
		// Reads a blog entry and returns an key/value pair array.
		//
		// Returns false on fail...
		global $sb_info;
		$blog_entry_data = array();
		
		$str = sb_read_file( $entryFile );
		$exploded_array = explode( '|', $str );
		
		if ( count( $exploded_array ) > 1 ) {
			if ( count( $exploded_array ) <= 6 ) {
				// Old List Format: subject, date, content, (mood, song)
				$blog_entry_data[ 'SUBJECT' ]		= $exploded_array[0];
				$blog_entry_data[ 'DATE' ]			= $exploded_array[1];
				$blog_entry_data[ 'CONTENT' ]		= $exploded_array[2];
				$blog_entry_data[ 'relatedlink' ]	= $exploded_array[3];
				$blog_entry_data[ 'VERSION' ]		= $sb_info[ 'version' ];
				
			} else {
				// New Format: key/value pairs
				//
				// The "keys/value" pairs can be in any order. Also, the only ones
				// that are required are VERSION, SUBJECT, DATE, and CONTENT.
				//
				// All the other keys are optional. Eventually the CATEGORY tag
				// will parse into an array of it's own (probably comma delimited...)
				//
				// VERSION, SUBJECT, DATE, CONTENT, (MOOD, SONG, CATEGORY, etc...)
				// Total count will be 14 items...

				$blog_entry_data = explode_with_keys( $str );
			}
			
			return( $blog_entry_data );
		} else {
			return( false ); // error
		}
	}
	
	function write_entry ( $blog_subject, $blog_text, $tb_ping, $updateFile, $blog_categories, $blog_relatedlink, $blog_date=NULL) {
		// Save new entry or update old entry
		//
		// $updateFile will either be NULL or the name of the file
		// which is being updated (i.e. entry040603-140634)
		global $blog_config, $sb_info;

		$filename=CONFIG_DIR.'~blog_entry_listing.tmp';
		sb_delete_file( $filename );
		
		$save_data = array();
		$save_data[ 'VERSION' ] = $sb_info[ 'version' ];
		$save_data[ 'SUBJECT' ] = clean_post_text( $blog_subject );
		$save_data[ 'CONTENT' ] = sb_parse_url( clean_post_text( $blog_text ) );

		if ( count( $blog_categories ) > 0 ) {
			$save_data[ 'CATEGORIES' ] = implode( ',', $blog_categories );
		}
		if ( $tb_ping !== '' ) {
			$save_data[ 'TB_PING' ] = clean_post_text( $tb_ping );
		}

		// Read more link
		if ( $blog_relatedlink !== '' ) {
			$save_data[ 'relatedlink' ] = clean_post_text( $blog_relatedlink );
		}
		
		$save_data[ 'IP-ADDRESS' ] = getIP(); // New 0.4.8

		if ( $updateFile == true ) {
			// Updating an entry
			//
			// We need to grab the date of the old entry.
			if ( file_exists( $updateFile . '.txt' ) ) {
				$oldEntryFile = $updateFile . '.txt';
			} elseif ( file_exists( $updateFile . '.txt.gz' ) ) {
				$oldEntryFile = $updateFile . '.txt.gz';
			}
			$oldEntryArray = blog_entry_to_array( $oldEntryFile );

			$save_data[ 'CREATEDBY' ] = $oldEntryArray[ 'CREATEDBY' ]; // New 0.5.0
			$save_data[ 'DATE' ] = $oldEntryArray[ 'DATE' ];
			$y = date('y', $save_data[ 'DATE' ] );
			$m = date('m', $save_data[ 'DATE' ] );
			//$permalink = 'index.php?y='.$y.'&amp;m='.$m.'&amp;entry='.substr($updateFile,strrpos($updateFile,'/')+1);
			$permalink = 'index.php?entry=' . str_replace(' ', '-', $save_data[ 'SUBJECT' ]);
			
			// Delete the old file
			sb_delete_file( $updateFile . '.txt' );
			sb_delete_file( $updateFile . '.txt.gz' );
			
			$entryFile = $updateFile . '.txt';
		} else {
			// Create an entry
			//
			// This is going to be a new entry.
			// We might need to create some directories.
			//
			// The directory and file structure is:
			// 'content/YY/MM/entryYYMMDD-HHMMSS.txt'
			// 'F j, Y, g:i a'
			if (!$blog_date) {
				$blog_date = time();
			}
			
			$save_data[ 'DATE' ] = $blog_date;
			$save_data[ 'CREATEDBY' ] = $_SESSION[ 'username' ]; // New 0.5.0
			
			$dir = CONTENT_DIR;
			$y = date('y', $blog_date);
			$m = date('m', $blog_date);
			
			$stamp = date('ymd-His', $blog_date);
			$entryFile = $dir.$y.'/'.$m.'/'.'entry'.$stamp.'.txt';
			
			//$permalink = 'index.php?y='.$y.'&amp;m='.$m.'&amp;entry=entry'.$stamp;
			$permalink = 'index.php?entry=' . str_replace(' ', '-', $save_data[ 'SUBJECT' ]);
		}
		
		// Implode the array
		$str = implode_with_keys( $save_data );
		
		// Save the file		
		$result = sb_write_file( $entryFile, $str );
		
		// Result
		if ( $result ) {
			// Send Pings
			$url_array = explode( ',', $blog_config->getTag('BLOG_PING_URLS') );
			if ( is_array( $url_array ) ) {
				for ( $i = 0; $i < count( $url_array ); $i++ ) {
					sb_ping( $url_array[ $i ] );
				}
			}
			
			return ( true );
		} else {
			// Error:
			// Probably couldn't create file...
			return ( $entryFile );
		}
	}
	
	
	function move_entry ( $oldTime, $newTime ) {
		// Change the date on an entry and move all comments and associated files:
		//
		// oldTime = unix timestamp
		// newTime = unix timestamp
		global $blog_config, $sb_info;
		
		// Delete blog entry cache file.
		sb_delete_file( CONFIG_DIR.'~blog_entry_listing.tmp' );
		
		// Create directory structure for new entry:
		// content/YY/MM/entryYYMMDD-HHMMSS.txt
		
		$dir = CONTENT_DIR;
		
		$oldY = date('y', $oldTime);
		$oldM = date('m', $oldTime);
		$oldStamp = date('ymd-His', $oldTime);
		
		$newY = date('y', $newTime);
		$newM = date('m', $newTime);
		$newStamp = date('ymd-His', $newTime);
		
		if ( sb_create_folder($dir.$newY) == false ) {
			return ( 'Couldn\'t create directory: '.$dir.$newY );
		}
		
		if ( sb_create_folder($dir.$newY.'/'.$newM) == false ) {
			return ( 'Couldn\'t create directory: '.$dir.$newY.'/'.$newM );
		}
		
		// Comment, Rating, and View Counter Folder
		if ( file_exists($dir.$oldY.'/'.$oldM.'/'.'entry'.$oldStamp) ) {
			if ( sb_create_folder($dir.$newY.'/'.$newM.'/'.'entry'.$newStamp) == false ) {
				return ( 'Couldn\'t create directory: '.$dir.$newY.'/'.$newM.'/'.'entry'.$newStamp );
			}
			
			sb_copy($dir.$oldY.'/'.$oldM.'/'.'entry'.$oldStamp,$dir.$newY.'/'.$newM.'/'.'entry'.$newStamp);
		}
	}
	
	function implode_with_keys( $arr, $delim='|' ) {
		return arrays::implode_key($arr, $delim);
	} 
	
	function explode_with_keys( $str, $delim='|' ) {	
		return arrays::explode_key($str, $delim);
	}

	function delete_dir( $dir ) {
		// Get listing of files in folder.
		$file_array = sb_folder_listing( $dir . '/', array( '.txt', '.gz' ) );
		if ( count( $file_array ) == 0 ) {
			// Directory is empty, delete it...
			sb_delete_directory( $dir );
		} else {
			for ( $i = 0; $i < count( $file_array ); $i++ ) {
				sb_delete_file( $dir . '/' . $file_array[$i] );
			}
			sb_delete_directory( $dir );
		}
	}
	
	function delete_entry( $entry, $path ) {
		$filename=CONFIG_DIR.'~blog_entry_listing.tmp';
		sb_delete_file( $filename );

		// Delete Entry File
		if ( file_exists( $path . $entry . '.txt' ) ) {
			$ok = sb_delete_file( $path . $entry . '.txt' );
		}
		if ( file_exists( $path . $entry . '.txt.gz' ) ) {
			$ok = sb_delete_file( $path . $entry . '.txt.gz' );
		}
		
		delete_dir( $path . $entry . '/comments');
		delete_dir( $path . $entry . '/trackbacks');
		delete_dir( $path . $entry);
		
		return ( $ok );
	}

	// ----------------
	// Rating Functions
	// ----------------
	
	function write_rating( $y, $m, $entry, $rating ) {
		// Save Rating
		$result_array = read_rating( $y, $m, $entry, $rating );
		if ( $result_array ) {
			$result_array[ 'points' ] = $result_array[ 'points' ] + $rating;
			$result_array[ 'votes' ] = $result_array[ 'votes' ] + 1;
		} else {
			$result_array = array();
			$result_array[ 'points' ] = $rating;
			$result_array[ 'votes' ] = 1;
		}
		
		$str = '';
		$keys = array_keys( $result_array );
		for ( $i = 0; $i < count( $keys ); $i++ ) {
			$key = $keys[ $i ];
			if ( $i > 0 ) {
				$str	.= '|';
			}
			$str	.= $key . '|' . $result_array[ $key ];
		}
		
		$filename = CONTENT_DIR.$y.'/'.$m.'/'.$entry.'/rating.txt';
		sb_write_file( $filename, $str );
	}
	
	function read_rating( $y, $m, $entry ) {
		// Read the rating.txt file and return the stored data.
		//
		// Returns NULL on fail.
		$rating_path = CONTENT_DIR.$y.'/'.$m.'/'.$entry.'/';
		$contents = sb_read_file( $rating_path . 'rating.txt' );
		
		if ( $contents ) {
			$result_array = array();
			
			$data_array = explode('|', $contents);
			$key_array = array( 'points', 'votes' );
			
			for ( $i = 0; $i < count( $data_array ); $i = $i + 2 ) {
				for ( $j = 0; $j < count( $key_array ); $j++ ) {
					if ( $data_array[ $i ] == $key_array[ $j ] ) {
						$key = $key_array[ $j ];
						$result_array[ $key ] = intval( $data_array[ $i+1 ] );
					}
				}
			}
			
			return( $result_array );
		}
	}
	
	function write_modifica ( $blog_subject, $blog_text, $tb_ping, $updateFile, $blog_categories, $blog_date=NULL, $filename) {
		sb_delete_file( $filename );
		write_entry( sb_stripslashes( $_POST['blog_subject'] ), sb_stripslashes( $_POST['blog_text'] ), sb_stripslashes( $_POST['tb_ping'] ), $_POST['entry'], $_POST[ "catlist" ], $faketime);
	}
?>
