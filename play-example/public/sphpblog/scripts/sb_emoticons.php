<?php
	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com
	
	// -------------------------------
	// Emoticon Code by NoWhereMan and Hydra	
	// -------------------------------
	
	// emoticons_check_tags($smile_path)
	// emoticons_load_tags ()
	// emoticons_load ()
	// emoticons_show ()
	
	// --------------------
	// Emoticon Functions
	// --------------------
	
	// This function checks for alternate smiley tags
	// EX: the image for :) could be the same of ^^ ^__^ :happy: or :smile:
	function emoticons_check_tags($smile_path) {
		// Loop through the emoticon array. Look for matching smile_path and emote['path']. Return associated tags.
		$emote_arr = emoticons_load_tags();
		
		if ( $emote_arr !== false ) {
			for ( $n = 0; $n < count( $emote_arr ); $n++ ) {
				if ($emote_arr[$n]['PATH']==$smile_path) {
					return( $emote_arr[$n]['TAGS'] );
				}
			}
		}
		
		return( false );
	}

	function emoticons_load_tags () {
		$str = sb_read_file( CONFIG_DIR.'emoticons.txt' );
		
		$emote_arr = array();
		if ($str) {
			$exploded_array = explode( '|', $str );
			
			if ( count( $exploded_array ) > 0 ) {
				for ( $i = 0; $i < count( $exploded_array ); $i++ ) {
					$emo = explode_with_keys( $exploded_array[$i], '=' );
					$tags = $emo['TAGS'];
					$tags = str_replace( '&#124;', '|', $tags );
					$tags = str_replace( '&#61;', '=', $tags );
					$emo['TAGS'] = $tags;
					array_push( $emote_arr, $emo);
				}
			} else {
				return( false );
			}			
		}

		return( $emote_arr );
	}
	
	function emoticons_load () {
		global $blog_theme;
		
		$emotepath_arr = Array();
		array_push( $emotepath_arr, 'themes/' . $blog_theme . '/images/emoticons/' );
		array_push( $emotepath_arr, 'interface/emoticons/' );
		array_push( $emotepath_arr, IMAGES_DIR.'emoticons/' );
		
		$emoteimage_arr = Array();
		for ( $i = 0; $i < count( $emotepath_arr ); $i++) {
			$dir = $emotepath_arr[$i];
			$temp_arr = sb_folder_listing( $dir, array(".gif", ".jpg", ".jpeg", ".png") );
			
			for ( $j = 0; $j < count( $temp_arr ); $j++ ) {
				array_push( $emoteimage_arr, $dir . $temp_arr[$j] );
			}
		}
		// echo('<pre>');
		// print_r($emoteimage_arr);
		// echo('</pre>');
		
		return( $emoteimage_arr );
	}
	
	function emoticons_show ($textfield='blog_text') {
		$emote_arr = emoticons_load_tags();
		
		$str = null;
		for ( $n = 0; $n < count( $emote_arr ); $n++ ) {
			$path = $emote_arr[ $n ][ 'PATH' ];
			$tags_str = $emote_arr[ $n ][ 'TAGS' ];
			$tags_arr = explode( ' ', $tags_str );
			$str .=  '<span onclick="Javascript:theform=(document.getElementById(\'comment_text\')==null)?document.getElementById(\'' . $textfield . '\'):document.getElementById(\'comment_text\');ins_emoticon(theform, \'' . encode_input_value(addslashes($tags_arr[0])) . '\');">' ."\n";
			$str .=  '<img src="' . $path . '" alt="'.encode_input_value($tags_str).'"title="'.encode_input_value($tags_str).'"  />' ."\n";
			$str .=  '</span>' ."\n";
		}
		
		$str_out = null;
		if ( $str != null ) {
			$str_out =  "<div>\n" . $str . "</div><p />\n";
		}
		
		echo( $str_out );
		return( $str_out );
	}
?>
