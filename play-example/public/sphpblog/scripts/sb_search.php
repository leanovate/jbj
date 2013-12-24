<?php 
	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com

	// ----------------
	// Search Functions
	// ----------------
	
	function search ( $search_str, $max_results=0 ) {
		// Search in contents feature a list of keywords separated by spaces
		// (c) 2004 Javier Gutirrez Chamorro (Guti), guti <at> ya <dot> com
		//
		// Simple PHP Version: 0.3.7
		// Search Version:   0.3.7
		//
	
		// Read entries by month, year and/or day. Generate HTML output.
		//
		// Used for the main Index page.
		global $blog_config, $user_colors;
		
		// To avoid server overload
		sleep(1);
		
		$output_str = '';
		
		$entry_file_array = blog_entry_listing();

		$words=@split( ' ', strtoupper( $search_str ) );
		
		if ( strlen( $search_str ) > 1) {
			for ( $i = 0; $i < count( $words ); $i++) {
				$words[$i] = trim($words[$i] );
			}
			
			if ( $max_results <= 0 ) {
				$max_results=30;
			}
			
			$results=0;
			// Loop through entry files
			for ( $i = 0; $i < count( $entry_file_array ); $i++ ) {
				if ( $results > $max_results ) {
					break;
				}
			
				list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
				$contents = sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
				$j = 0;
				$found = true;
				$text = strtoupper( $contents );
				while ( ( $j<count( $words ) ) && ( $found ) ) {
					if ( $words[$j] !== '' ) {
						$found = $found && ( strpos( $text, $words[$j] ) !== false );
					}
					$j++;
				}
				if ( $found ) {
					$results++;
					$blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
					$output_str  .= '<a href="index.php?entry=' . sb_strip_extension( $entry_filename ) . '" title="' . format_date( $blog_entry_data[ 'DATE' ] ) . '">' . $blog_entry_data[ 'SUBJECT' ] . '</a><br />';
				}
				// Search Comments
				if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == true ) {
					$comment_file_array = sb_folder_listing( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/', array( '.txt', '.gz' ) );
	 
					for ( $k = 0; $k < count( $comment_file_array ); $k++ ) {
						$comment_filename =  $comment_file_array[ $k ];
						//We only want to search inside comments, not the counter
						if ( strpos($comment_filename, 'comment') === 0 )
						{
							$contents_comment = sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/' . $comment_filename );
							$found_in_comment = true;
							$l = 0;
							$text = strtoupper( $contents_comment );
							while ( ( $l< count( $words ) ) && ( $found_in_comment ) ) {
								if ( $words[$l] !== '' ) {
									$found_in_comment = $found_in_comment && ( strpos( $text, $words[$l] ) !== false );
								}
								$l++;
							}
							if ( $found_in_comment ) {
								$results++;
								if ( $found == false ) {
									// list( $blog_subject, $blog_date, $blog_text ) = explode('|', ( $contents ) );
									$blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
									$output_str  .= $blog_entry_data[ 'SUBJECT' ] . '<br />';
								}
								
								// list( $comment_author, $comment_date, $comment_text ) = explode('|', ( $contents_comment ) );
								$comment_entry_data = comment_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/' . $comment_filename );

								global $theme_vars;
								$output_str  .= '&nbsp;&nbsp;&nbsp;<a href="comments.php?y=' . $year_dir . '&amp;m=' . $month_dir . '&amp;entry=' . sb_strip_extension( $entry_filename ) . '" title="' . format_date( $comment_entry_data[ 'DATE' ] ) . '">' . $comment_entry_data[ 'NAME' ] . '</a><br />';
							}
						}
					}
				}
			}
			// Search static pages
			$static_file_array = sb_folder_listing( CONTENT_DIR.'static/', array( '.txt', '.gz' ) );
			for ( $i = 0; $i < count( $static_file_array ); $i++ ) {
				$static_filename =  $static_file_array[ $i ];
				$contents_static = sb_read_file( CONTENT_DIR.'static/' . $static_filename );
				$found_in_static = true;
				$j = 0;
				$text = strtoupper( $contents_static );
				while ( ( $j< count( $words ) ) && ( $found_in_static ) ) {
					if ( $words[$j] !== '' ) {
						$found_in_static = $found_in_static && ( strpos( $text, $words[$j] ) !== false );
					}
					$j++;
				}
				if ( $found_in_static ) {
					$results++;
					$blog_static_data = static_entry_to_array( CONTENT_DIR.'static/' . $static_filename );
					$output_str  .= '<a href="static.php?page=' . sb_strip_extension( $static_filename ) . '" title="' . format_date( $blog_static_data[ 'DATE' ] ) . '">' . $blog_static_data[ 'SUBJECT' ] . '</a><br />';
				}
			}
		}
		return ( $output_str );
	}
?>
