<?php 
	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com

	// -------------------------
	// Form Validation Functions
	// -------------------------
	
	function isInt($value, $empty) {
		if (strlen($value) == 0) { return $empty; }
		
		return preg_match('!^\d+$!', $value);
	}
	
	function isNumber($value, $empty) {
		if (strlen($value) == 0) { return $empty; }
		
		return preg_match('!^\d+(\.\d+)?$!', $value);
	}
	
	function isDate($value, $empty) {
		if (strlen($value) == 0) { return $empty; }
	
		return strtotime($value) != -1;
	}
	
	function isEmail($value, $empty) {
		if (strlen($value) == 0) { return $empty; }
	
		// in case value is several addresses separated by newlines
		$_addresses = preg_split('![\n\r]+!', $value);
	
		foreach($_addresses as $_address) {
			$_is_valid = !(preg_match('!@.*@|\.\.|\,|\;!', $_address) ||
				!preg_match('!^.+\@(\[?)[a-zA-Z0-9\.\-]+\.([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$!', $_address));
			
			if (!$_is_valid) { return false; }
		}
		return true;
	}
	
	function notEmpty($value, $empty) {
		return strlen($value) > 0;
	}
	
	// ----------------------
	// Form Utility Functions
	// ----------------------
	
	
	/**
	* Strips slashes if magic quotes is on.
	*
	* @param		string $str
	* @return		string
	*/
	function smartstripslashes($str) {
		if ( get_magic_quotes_gpc() == true ) {
			$str = stripslashes($str);
		}
		return $str;
	}
	function sb_stripslashes($str) {
		return smartstripslashes($str);
	}
	
	/**
	* Adds slashes if magic quotes is on.
	*
	* @param		string $str
	* @return		string
	*/
	function smartaddslashes($str) {
		if ( get_magic_quotes_gpc() == false ) {
			$str = addslashes( $str );
		}
		return $str;
	}
	function sb_addslashes($str) {
		return smartaddslashes($str);
	}
	
	
	function encode_input_value( $str ) {
	
		global $lang_string;
		$str = @htmlspecialchars( $str, ENT_QUOTES, $lang_string[ 'php_charset' ] );

		return ( $str );
	}

	// ---------------------
	// HTML Markup Functions
	// ---------------------
	
	function HTML_dropdown( $label=false, $id, $itemArray, $add_returns=true, $onchange=null, $width=0, $size=0, $multiple=false, $disabled=false ) {
		// This function creates a standard HTML select form.
		// Can be used for drop-downs, or selection boxes.
		
		$str = '';
		// <label for="$id">$label</label><br />
		if ( isset( $label ) && $label !== false ) {
			$str .= '<label for="'.$id.'">'.$label.'</label>';
			if ( $add_returns ) {
				$str .= '<br />';
			}
			$str .= "\n";
		}
		
		// <select id="$id" name="$id" size="$size" multiple disabled style="width: 100px;">
		$str .= '<select id="'.$id.'" name="'.$id.'"';
		if ( $size > 0 ) {
			$str .= ' size="'.$size.'"';
		}
		if ( $multiple == true ) {
			$str .= ' multiple';
		}
		if ( $disabled == true ) {
			$str .= ' disabled';
		}
		if ( $width > 0) {
			$str .= ' style="width: '.$width.'px;"';
		}
		if ( isset( $onchange ) ) {
			$str .= ' onchange="'.$onchange.'"';
		}
		$str .= '>' . "\n";
		
		// <option label="$label" value="$value" disabled selected>$string</option>
		// <option label="$label" value="$value" disabled selected>$label</option>
		for ( $i = 0; $i < count( $itemArray ); $i++ ) {
			$item = $itemArray[$i];
			
			$str .= "\t" . '<option label="'.$item['label'].'" value="'.$item['value'].'"';
			if ( array_key_exists( 'disabled', $item ) && $item['disabled'] == true ) {
				$str .= ' disabled';
			}
			if ( array_key_exists( 'selected', $item ) && $item['selected'] == true ) {
				$str .= ' selected';
			}
			if ( array_key_exists( 'string', $item ) ) {
				$str .= '>'.$item['string'].'</option>' . "\n";
			} else {
				$str .= '>'.$item['label'].'</option>' . "\n";
			}
		}
		$str .= '</select>';
		if ( $add_returns ) {
			$str .= '<p />';
		}
		$str .= "\n";
		
		return $str;
	}
	
	function HTML_checkbox( $label=false, $id, $value=null, $add_returns=true, $onchange=null, $checked=false, $disabled=false ) {
		$str = HTML_input( false, $id, $value, false, 'checkbox', null, null, $onchange, 0, $disabled, false, $checked );
		
		if ( isset( $label ) && $label !== false ) {
			$str .= '<label for="'.$id.'">'.$label.'</label>';
			if ( $add_returns ) {
				$str .= '<br />';
			}
			$str .= "\n";
		}
		
		return $str;
	}
	
	function HTML_input( $label=false, $id, $value=null, $add_returns=true, $type='text', $size=null, $maxlength=null, $onchange=null, $width=0, $disabled=false, $autocomplete=false, $checked=false ) {
		// This function creates a standard HTML input form.
		
		$str = '';
		if ( isset( $label ) && $label !== false ) {
			$str .= '<label for="'.$id.'">'.$label.'</label>';
			if ( $add_returns ) {
				$str .= '<br />';
			}
			$str .= "\n";
		}
		
		// <input name="name" id="id" type="text" value="value" size="2" maxlength="2" onchange="" disabled>
		$str .= '<input name="'.$id.'" id="'.$id.'" type="'.$type.'"';
		if ( isset( $value ) ) {
			$str .= ' value="'. addslashes( encode_input_value( $value ) ) .'"';
		}
		if ( $size > 0 ) {
			$str .= ' size="'.$size.'"';
		}
		if ( $maxlength > 0 ) {
			$str .= ' maxlength="'.$maxlength.'"';
		}
		if ( isset( $onchange ) ) {
			$str .= ' onchange="'.$onchange.'"';
		}
		if ( $width > 0) {
			$str .= ' style="width: '.$width.'px;"';
		}
		if ( !$autocomplete ) {
			$str .= ' autocomplete="OFF"';
		}
		if ( $disabled ) {
			$str .= ' disabled';
		}
		if ( $checked ) {
			$str .= ' checked';
		}
		$str .= '>' . "\n";
		
		if ( $add_returns ) {
			$str .= '<p />';
		}
		$str .= "\n";
		
		return $str;
	}
?>