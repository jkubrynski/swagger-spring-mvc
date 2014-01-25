package eu.codearte.swagger.sample;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Jakub Kubrynski / 2014-01-25
 */
@Controller
@RequestMapping("/sample")
@Api("Sample operations")
public class FirstController {

	@RequestMapping(value = "/void", method = RequestMethod.GET)
	public void voidMethod() {
		return;
	}
}
