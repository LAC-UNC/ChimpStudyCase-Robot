package com.main;

import java.util.ArrayList;
import java.util.List;

import resources.PlainPiston;
import resources.Position;
import utils.FinalContainer;
import utils.InitialContainer;

import com.lac.petrinet.configuration.providers.PNMLConfigurationReader;
import com.lac.petrinet.core.PetriNet;
import com.lac.petrinet.exceptions.PetriNetException;
import com.productioncell.dummies.v2.AbiertoPistonDummy;
import com.productioncell.dummies.v2.AdelantePistonDummy;
import com.productioncell.dummies.v2.AtrasPistonDummy;
import com.productioncell.dummies.v2.ErrorChecker;
import com.productioncell.dummies.v2.ErrorHandlerPistonDummy;

public class ProductionCellChimpV2Main {
	public static void main(String[] args) throws PetriNetException {
		
		// Resources
		PlainPiston a = new PlainPiston("PA", 200, 20, 15, 10);
		PlainPiston b = new PlainPiston("PB", 150, 30, 12, 5);
		PlainPiston c = new PlainPiston("PC", 170, 40, 8, 4);
		PlainPiston d = new PlainPiston("PD", 120, 80, 14, 10);
		
		InitialContainer initialContainer = new InitialContainer();
		Position one = new Position("one");
		Position two = new Position("two");
		Position three = new Position("three");
		FinalContainer finalContainer = new FinalContainer();
	
		// Petri Net creation and configuration
		PNMLConfigurationReader pnmlConfigurator = new PNMLConfigurationReader();
		PetriNet pn = pnmlConfigurator.loadConfiguration(ProductionCellChimpV2Main.class.getClassLoader()
				.getResource("resources/modelov2.2.pnml").getPath());
		
		pn.addInputEventAlias("PA-error-found", "t22");
		pn.addInputEventAlias("PB-error-found", "t42");
		pn.addInputEventAlias("PC-error-found", "t44");
		pn.addInputEventAlias("PD-error-found", "t46");
		pn.addInputEventAlias("PA-end-forward", "t2");
		
		pn.addOutputEventAlias("PA-start-forward", "t1");
		pn.addOutputEventAlias("PB-start-forward", "t16");
		pn.addOutputEventAlias("PC-start-forward", "t24");
		pn.addOutputEventAlias("PD-start-forward", "t33");
		pn.addOutputEventAlias("PA-start-error-checking", "t8");
		
		// Dummies Adelante
		AdelantePistonDummy adelanteA = new AdelantePistonDummy("PA-end-forward", a , initialContainer, one);
		AdelantePistonDummy adelanteB = new AdelantePistonDummy("t21", b, one, two);
		AdelantePistonDummy adelanteC = new AdelantePistonDummy("t28", c, two, three);
		AdelantePistonDummy adelanteD = new AdelantePistonDummy("t37", d, three, finalContainer);
		
		// Dummies Atras
		AtrasPistonDummy atrasA = new AtrasPistonDummy("t3", a);
		AtrasPistonDummy atrasB = new AtrasPistonDummy("t18", b);
		AtrasPistonDummy atrasC = new AtrasPistonDummy("t26", c);
		AtrasPistonDummy atrasD = new AtrasPistonDummy("t35", d);
		
		// Dummies Abierto
		AbiertoPistonDummy abiertoA = new AbiertoPistonDummy("t4", a);
		AbiertoPistonDummy abiertoB = new AbiertoPistonDummy("t17", b);
		AbiertoPistonDummy abiertoC = new AbiertoPistonDummy("t25", c);
		AbiertoPistonDummy abiertoD = new AbiertoPistonDummy("t34", d);
		
		// Dummies Error checker
		ErrorChecker errorCheckerA = new ErrorChecker("t11", a);
		ErrorChecker errorCheckerB = new ErrorChecker("t15", b);
		ErrorChecker errorCheckerC = new ErrorChecker("t31", c);
		ErrorChecker errorCheckerD = new ErrorChecker("t40", d);
		
		// Dummies Error Handler
		ErrorHandlerPistonDummy errorHandlerA = new ErrorHandlerPistonDummy("t5", a);
		ErrorHandlerPistonDummy errorHandlerB = new ErrorHandlerPistonDummy("t10", b);
		ErrorHandlerPistonDummy errorHandlerC = new ErrorHandlerPistonDummy("t9", c);
		ErrorHandlerPistonDummy errorHandlerD = new ErrorHandlerPistonDummy("t7", d);
		
		// Relacionar dummies y transiciones Informadas.		
		pn.assignDummy("PA-start-forward", adelanteA);
		pn.assignDummy("PB-start-forward", adelanteB);
		pn.assignDummy("PC-start-forward", adelanteC);
		pn.assignDummy("PD-start-forward", adelanteD);
		
		pn.assignDummy("t2", abiertoA);
		pn.assignDummy("t21", abiertoB);
		pn.assignDummy("t28", abiertoC);
		pn.assignDummy("t37", abiertoD);
		
		pn.assignDummy("t4", atrasA);
		pn.assignDummy("t17", atrasB);
		pn.assignDummy("t25", atrasC);
		pn.assignDummy("t34", atrasD);
		
		pn.assignDummy("t8", errorCheckerA );
		pn.assignDummy("t13", errorCheckerB );
		pn.assignDummy("t30", errorCheckerC );
		pn.assignDummy("t39", errorCheckerD );
		
		pn.assignDummy("t41", errorHandlerA);
		pn.assignDummy("t14", errorHandlerB);
		pn.assignDummy("t23", errorHandlerC);
		pn.assignDummy("t32", errorHandlerD);
		
		List<String> groupOne = new ArrayList<String>();
		List<String> groupTwo = new ArrayList<String>();
		List<String> groupThree = new ArrayList<String>();
		List<String> groupFour = new ArrayList<String>();
		
		groupOne.add("PA-start-error-checking");
		groupOne.add("PA-start-forward");

		groupTwo.add("t13");
		groupTwo.add("t4");
		groupTwo.add("t16");
		
		groupThree.add("t30");
		groupThree.add("t17");
		groupThree.add("t24");
		
		groupFour.add("t39");
		groupFour.add("t25");
		groupFour.add("t33");
		
		pn.addTransitionNameGroup(groupOne);
		pn.addTransitionNameGroup(groupTwo);
		pn.addTransitionNameGroup(groupThree);
		pn.addTransitionNameGroup(groupFour);

		pn.startListening();
	}
	
	
}
