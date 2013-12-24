<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	
	global $logged_in;
	$logged_in = logged_in( false, true );

	$page_title = _sb('stats_title');
	require_once('scripts/sb_header.php');

	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	function sort_views ( $a, $b ) {
		if ( $a[ 'views' ] < $b[ 'views' ] ) {
			return( 1 );
		} else {
			return( -1 );
		}
	}
	
	function sort_comments ( $a, $b ) {
		if ( $a[ 'comments' ] < $b[ 'comments' ] ) {
			return( 1 );
		} else {
			return( -1 );
		}
	}

	function sort_trackbacks ( $a, $b ) {
		if ( $a[ 'trackbacks' ] < $b[ 'trackbacks' ] ) {
			return( 1 );
		} else {
			return( -1 );
		}
	}
	
	function sort_votes ( $a, $b ) {
		if ( $a[ 'votes' ] < $b[ 'votes' ] ) {
			return( 1 );
		} else {
			return( -1 );
		}
	}

	function sort_rates ( $a, $b ) {
		if ( $a[ 'rates' ] < $b[ 'rates' ] ) {
			return( 1 );
		} else {
			return( -1 );
		}
	}

	
	function generate_stats ( ) {
		global $blog_config;

		// To avoid server overload
		sleep(1);
		
		$output_str = '';
		$total_number_entries = 0;
		$total_number_comments = 0;
		$total_number_statics = 0;
		$total_number_votes = 0;
		$total_number_trackbacks = 0;
		$total_bytes_entries = 0;
		$total_bytes_comments = 0;
		$total_bytes_statics = 0;
		$total_bytes_trackbacks = 0;
		$total_bytes_votes = 0;
		$total_words_entries = 0;
		$total_words_comments = 0;
		$total_words_statics = 0;
		
		$entry_file_array = blog_entry_listing();

		// Loop through entry files
		for ( $i = 0; $i < count( $entry_file_array ); $i++ ) {
			list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
			$total_number_entries++;
			$contents=blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
			$entries[ $i ][ 'subject' ]=$contents[ 'SUBJECT' ];
			$total_words_entries+=str_word_count( sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename ) );
			$total_bytes_entries+=filesize( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
			$entries[ $i ][ 'filename' ]=$entry_filename;

			//Count votes
			$rating_array = read_rating( $year_dir, $month_dir, sb_strip_extension( $entry_filename, array( '.txt', '.gz' ) ) );
			$total_bytes_votes+=@filesize( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename, array( '.txt', '.gz' ) ) . '/rating.txt' );
			$total_number_votes+=$rating_array[ 'votes' ];
			$entries[ $i ][ 'votes' ] = $rating_array[ 'votes' ];
			if ($rating_array[ 'votes' ]>0) {
				$entries[ $i ][ 'rates' ] = $rating_array[ 'points' ] / $rating_array[ 'votes' ];
			}
			else {
				$entries[ $i ][ 'rates' ] = 0;
			}
			unset( $rating_array );
			
			// Count comments
			if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == true ) {
				$comment_file_array = sb_folder_listing( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/', array( '.txt', '.gz' ) );
				for ( $k = 0; $k < count( $comment_file_array ); $k++ ) {
					$total_number_comments++;
					$total_words_comments+=str_word_count( sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/' . $comment_file_array[ $k ] ) );
					$total_bytes_comments+=filesize( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/' . $comment_file_array[ $k ] );
				}
				$entries[ $i ][ 'comments' ]=$k;
				$entries[ $i ][ 'views' ]=sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/view_counter.txt');
			}
			else {
				$entries[ $i ][ 'comments' ]=0;
				$entries[ $i ][ 'views' ]=0;
			}
			unset( $comment_file_array );
			
			// Count trackbacks
			$entries[ $i ][ 'trackbacks' ]=0;
			unset( $trackback_file_array );
		}
		unset( $entry_file_array );
		
		// Count static pages
		$static_file_array = sb_folder_listing( CONTENT_DIR.'static/', array( '.txt', '.gz' ) );
		for ( $i = 0; $i < count( $static_file_array ); $i++ ) {
			$total_number_statics++;
			$total_words_statics+=str_word_count( sb_read_file( CONTENT_DIR.'static/' . $static_file_array[ $i ] ) );
			$total_bytes_statics+=filesize( CONTENT_DIR.'static/' . $static_file_array[ $i ] );
		}
		unset( $static_file_array );

		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('stats_title') . ' - ' . _sb('general');
		$entry_array[ 'entry' ]	 .= sprintf( _sb('entry_info'), number_format( $total_number_entries, 0 ), number_format( $total_words_entries, 0 ), number_format( $total_bytes_entries, 0 ) ) . '.<br />';
		$entry_array[ 'entry' ]	 .= sprintf( _sb('comment_info'), number_format( $total_number_comments, 0 ), number_format( $total_words_comments, 0 ), number_format( $total_bytes_comments, 0 ) ) . '.<br />';
		$entry_array[ 'entry' ]	 .= sprintf( _sb('trackback_info'), number_format( $total_number_trackbacks, 0 ), number_format( $total_bytes_trackbacks, 0 ) ) . '.<br />';
		$entry_array[ 'entry' ]	 .= sprintf( _sb('static_info'), number_format( $total_number_statics, 0 ), number_format( $total_words_statics, 0 ), number_format( $total_bytes_statics, 0 ) ) . '.<br />';
		$entry_array[ 'entry' ]	 .= sprintf( _sb('vote_info'), number_format( $total_number_votes, 0 ), number_format( $total_bytes_votes, 0 ) ) . '.<br />';
		echo( theme_staticentry( $entry_array ) );		
		
		if ( $blog_config->getTag('BLOG_ENABLE_VOTING') == true ) {
			if ( is_array( $entries ) ) {
				$entry_array = array();
				$entry_array[ 'subject' ] = _sb('most_rated_entries');
				usort( $entries, 'sort_rates' );
				for ( $i=0; $i<min(10, $total_number_entries); $i++) {
					$entry_array[ 'entry' ]	 .= '<a href="index.php?entry=' . sb_strip_extension( $entries[ $i ][ 'filename' ] ) . '">' . $entries[ $i ][ 'subject' ] . '</a> (' . number_format( $entries[ $i ][ 'rates' ], 2 ) . ').<br />';
				}
				echo( theme_staticentry( $entry_array ) );
			
				$entry_array = array();
				$entry_array[ 'subject' ] = _sb('most_voted_entries');
				usort( $entries, 'sort_votes' );
				for ( $i=0; $i<min(10, $total_number_comments); $i++) {
					$entry_array[ 'entry' ]	 .= '<a href="index.php?entry=' . sb_strip_extension( $entries[ $i ][ 'filename' ] ) . '">' . $entries[ $i ][ 'subject' ] . '</a> (' . number_format( $entries[ $i ][ 'votes' ], 0 ) . ').<br />';
				}
				echo( theme_staticentry( $entry_array ) );
			}
		}
		
		if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == true ) {
			if ( is_array( $entries ) ) {
				$entry_array = array();
				$entry_array[ 'subject' ] = _sb('most_viewed_entries');
				usort( $entries, 'sort_views' );
				for ( $i=0; $i<min(10, $total_number_entries); $i++) {
					$entry_array[ 'entry' ]	 .= '<a href="index.php?entry=' . sb_strip_extension( $entries[ $i ][ 'filename' ] ) . '">' . $entries[ $i ][ 'subject' ] . '</a> (' . number_format( $entries[ $i ][ 'views' ], 0 ) . ').<br />';
				}
				echo( theme_staticentry( $entry_array ) );
			
				$entry_array = array();
				$entry_array[ 'subject' ] = _sb('most_commented_entries');
				usort( $entries, 'sort_comments' );
				for ( $i=0; $i<min(10, $total_number_comments); $i++) {
					$entry_array[ 'entry' ]	 .= '<a href="index.php?entry=' . sb_strip_extension( $entries[ $i ][ 'filename' ] ) . '">' . $entries[ $i ][ 'subject' ] . '</a> (' . number_format( $entries[ $i ][ 'comments' ], 0 ) . ').<br />';
				}
				echo( theme_staticentry( $entry_array ) );
			}
		}
	}
	
	function page_content() {
		echo( generate_stats() );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
