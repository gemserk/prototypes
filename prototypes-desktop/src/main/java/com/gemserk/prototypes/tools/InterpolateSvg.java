package com.gemserk.prototypes.tools;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gemserk.commons.svg.inkscape.DocumentParser;
import com.gemserk.commons.svg.processors.SvgDocumentProcessor;
import com.gemserk.commons.svg.processors.SvgElementProcessor;

public class InterpolateSvg {
	
	public static void main(String[] args) {
		
		ArrayList<Element> frameElements = new ArrayList<Element>();
		
		Document document = new DocumentParser().parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("svginterpolation/inkdrop.svg"));
		
		SvgDocumentProcessor svgDocumentProcessor = new SvgDocumentProcessor();
		
		svgDocumentProcessor.process(document, new SvgElementProcessor() {
			@Override
			public boolean processElement(Element element) {
				return super.processElement(element);
			}
		});

	}

}
