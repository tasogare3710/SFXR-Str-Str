// Copyright (c) 2013, Florian DORMONT/Eiyeron Fulmicendii tasogare All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
package com.github.tasogare.sfxr.app;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingUtilities.getUIActionMap;
import static javax.swing.SwingUtilities.getUIInputMap;
import static javax.swing.SwingUtilities.updateComponentTreeUI;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.System.Logger.Level;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComponentInputMap;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

import com.github.tasogare.sfxr.engine.FX;
import com.github.tasogare.sfxr.engine.Preset;
import com.github.tasogare.sfxr.engine.Sound;
import com.github.tasogare.sfxr.engine.Synth;
import com.github.tasogare.sfxr.engine.WaveForm;
import com.github.tasogare.sfxr.laf.InvertTheme;
import com.github.tasogare.sfxr.laf.IronTheme;
import com.github.tasogare.sfxr.laf.MatchaTheme;
import com.github.tasogare.sfxr.laf.OldschoolTheme;
import com.github.tasogare.sfxr.laf.SilverTheme;

/**
 * rewritten SFXR-Plus-Plus Application class.
 * 
 * @author Eiyeron, tasogare
 * @version 1.00
 */
public class Application {
	private final class BeepAction extends AbstractAction {
		private static final long serialVersionUID = -6197788585625310326L;

		private BeepAction() {
			super(FX.BEEP.toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng, FX.BEEP);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class ExplosionAction extends AbstractAction {
		private static final long serialVersionUID = 3915220212600311631L;

		private ExplosionAction() {
			super(FX.EXPLOSION.toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng, FX.EXPLOSION);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class ExportAction extends AbstractAction {
		private static final long serialVersionUID = 5958808792613268506L;

		ExportAction() {
			super("export");
			putValue(SHORT_DESCRIPTION, "export wave format file");
			putValue(MNEMONIC_KEY, KeyEvent.VK_X);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			logger.log(Level.TRACE, "ExportAction#actionPerformed");

			disableMenuBar();

			var chooser = new JFileChooser();
			var filter = new FileNameExtensionFilter("wave file", "wav");
			chooser.setFileFilter(filter);
			if (chooser.showSaveDialog(primaryFrame) == JFileChooser.APPROVE_OPTION) {
				var file = getFile(chooser);
				var splitedRng = rng.split();
				executor.execute(() -> {
					try (var os = new BufferedOutputStream(new FileOutputStream(file))) {
						var syn = new Synth(preset, splitedRng);
						var sound = syn.createSound(splitedRng);
						Application.this.write(sound, os);
					} catch (IOException ex) {
						logger.log(Level.ERROR, ex);
					}
					SwingUtilities.invokeLater(Application.this::enableMenuBar);
				});
			} else {
				enableMenuBar();
			}
		}

		private File getFile(JFileChooser chooser) {
			var file = chooser.getSelectedFile();
			String path = file.getPath();
			if (path.toLowerCase().endsWith(".wav")) {
				return file;
			} else {
				return new File(path + ".wav");
			}
		}
	}

	private final class FileMenuAction extends AbstractAction {
		private static final long serialVersionUID = -246651351683718566L;

		FileMenuAction() {
			super("file");

			putValue(MNEMONIC_KEY, KeyEvent.VK_F);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class FontAction extends AbstractAction {
		private static final long serialVersionUID = -957639196813922392L;

		private FontAction() {
			super("font");
			putValue(SHORT_DESCRIPTION, "select user font");
			putValue(MNEMONIC_KEY, KeyEvent.VK_N);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			var worker = new FontActionWorker();
			executor.execute(worker);
		}
	}

	private final class FontActionWorker extends SwingWorker<JPanel, String> {
		private final JDialog fontSelector;

		private FontActionWorker() {
			fontSelector = new JDialog(primaryFrame);
			fontSelector.setLayout(new BorderLayout());
			fontSelector.setModalityType(ModalityType.APPLICATION_MODAL);
			disableMenuBar();
		}

		private JPanel createMainPanel(String[] fontNames, Font defaultFont, int defaultFontIndex) {
			var mainPane = new JPanel();
			mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));

			var fontFamilyComboBox = new JComboBox<>(fontNames);
			var renderer = new FontCellRenderer<String>();
			fontFamilyComboBox.setRenderer(renderer);

			if (defaultFontIndex >= 0) {
				fontFamilyComboBox.setSelectedIndex(defaultFontIndex);
			}

			var fontSizeTextField = new JTextField(3);
			int defaultSize = defaultFont.getSize();
			fontSizeTextField.setText(String.valueOf(defaultSize));

			var familyPane = new JPanel();
			familyPane.setLayout(new BoxLayout(familyPane, BoxLayout.LINE_AXIS));
			familyPane.add(new JLabel("family"));
			familyPane.add(fontFamilyComboBox);
			familyPane.add(new JLabel("size"));
			familyPane.add(fontSizeTextField);

			mainPane.add(familyPane);

			var plainButton = new JToggleButton("plain");
			plainButton.setFont(defaultFont);
			plainButton.setSelected(defaultFont.isPlain());

			var boldButton = new JToggleButton("bold");
			boldButton.setFont(defaultFont);
			boldButton.setSelected(defaultFont.isBold());

			var italicButton = new JToggleButton("italic");
			italicButton.setFont(defaultFont);
			italicButton.setSelected(defaultFont.isItalic());

			var boldItalicButton = new JToggleButton("bold italic");
			boldItalicButton.setFont(defaultFont);
			boldItalicButton.setSelected(defaultFont.isBold() && defaultFont.isItalic());

			var styleButtonGroup = new ButtonGroup();
			styleButtonGroup.add(plainButton);
			styleButtonGroup.add(boldButton);
			styleButtonGroup.add(italicButton);
			styleButtonGroup.add(boldItalicButton);

			var stylePane = new JPanel();
			stylePane.setLayout(new BoxLayout(stylePane, BoxLayout.LINE_AXIS));
			stylePane.add(new JLabel("style"));
			stylePane.add(plainButton);
			stylePane.add(boldButton);
			stylePane.add(italicButton);
			stylePane.add(boldItalicButton);

			mainPane.add(stylePane);

			var okButton = new JButton(new AbstractAction("ok") {
				private static final long serialVersionUID = 3938079351662048307L;

				@Override
				public void actionPerformed(ActionEvent e) {
					var family = (String) fontFamilyComboBox.getSelectedItem();
					var text = fontSizeTextField.getText();
					int size = Integer.parseInt(text);

					int style = Font.PLAIN;
					if (plainButton.isSelected()) {
						style = Font.PLAIN;
					} else if (boldButton.isSelected()) {
						style = Font.BOLD;
					} else if (italicButton.isSelected()) {
						style = Font.ITALIC;
					} else if (boldItalicButton.isSelected()) {
						style = Font.BOLD | Font.ITALIC;
					}
					selectedFont = Optional.of(new Font(family, style, size));
					fontSelector.setVisible(false);
				}
			});

			var cancelButton = new JButton("cancel");
			cancelButton.addActionListener(_ -> fontSelector.setVisible(false));

			var buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
			buttonPane.add(okButton);
			buttonPane.add(cancelButton);
			mainPane.add(buttonPane);

			return mainPane;
		}

		@Override
		public JPanel doInBackground() {
			publish("start search fonts");
			var displayLocale = Locale.getDefault(Locale.Category.DISPLAY);
			var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			var fontNames = ge.getAvailableFontFamilyNames(displayLocale);
			publish("end search fonts");

			publish("start set selected font");
			var defaultFont = selectedFont.orElseGet(() -> DEFAULT_USER_SELECTED_FONT);
			publish("end set selected font");

			publish("start search selected font index");
			var name = defaultFont.getName();
			int defaultFontIndex = Arrays.binarySearch(fontNames, name, String::compareTo);
			publish("end search selected font index");

			publish("start create dialog content");
			var mainPanel = createMainPanel(fontNames, defaultFont, defaultFontIndex);
			publish("end create dialog content");
			return mainPanel;
		}

		@Override
		protected void done() {
			JPanel contentPane = null;
			try {
				contentPane = get();
			} catch (InterruptedException | ExecutionException e) {
				logger.log(Level.ERROR, e);
			}
			requireNonNull(contentPane);

			fontSelector.add(contentPane, BorderLayout.CENTER);
			fontSelector.pack();
			logger.log(Level.INFO, "フォント選択準備完了");
			fontSelector.setVisible(true);
			logger.log(Level.INFO, "フォント選択終了");
			selectedFont.ifPresent(font -> {
				resizeStanderdLookAndFeelFont(UIManager.getLookAndFeel(), font);
				SwingUtilities.updateComponentTreeUI(primaryFrame.getContentPane());
				SwingUtilities.updateComponentTreeUI(primaryFrame.getRootPane());
				SwingUtilities.updateComponentTreeUI(primaryFrame.getLayeredPane());
				SwingUtilities.updateComponentTreeUI(primaryFrame.getGlassPane());
				selectedFont = Optional.of(font);
			});
			enableMenuBar();
		}

		@Override
		protected void process(List<String> chunks) {
			for (var chunk : chunks) {
				logger.log(Level.INFO, chunk);
			}
		}
	}

	private final class HurtAction extends AbstractAction {
		private static final long serialVersionUID = -743617220096711709L;

		private HurtAction() {
			super(FX.HURT.toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng, FX.HURT);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class JumpAction extends AbstractAction {
		private static final long serialVersionUID = 1410143921073432571L;

		private JumpAction() {
			super(FX.JUMP.toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng, FX.JUMP);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class LaserAction extends AbstractAction {
		private static final long serialVersionUID = -3731073588258362979L;

		private LaserAction() {
			super(FX.LASER.toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng, FX.LASER);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class LookAndFeelMenuAction extends AbstractAction {
		private static final long serialVersionUID = 4298084177501382095L;

		LookAndFeelMenuAction() {
			super("theme");

			putValue(MNEMONIC_KEY, KeyEvent.VK_T);
			setEnabled(UIManager.getLookAndFeel().getName().contains("Metal"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class MutateAction extends AbstractAction {
		private static final long serialVersionUID = -246651351683718566L;

		MutateAction() {
			super("mutate");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.mutate(rng);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class OpenAction extends AbstractAction {
		private static final long serialVersionUID = -246651351683718566L;

		OpenAction() {
			super("open");
			putValue(SHORT_DESCRIPTION, "open sfp file");
			putValue(MNEMONIC_KEY, KeyEvent.VK_P);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			logger.log(Level.TRACE, "OpenAction#actionPerformed");

			var chooser = new JFileChooser();
			var filter = new FileNameExtensionFilter("sfp file", "sfp");
			chooser.addChoosableFileFilter(filter);
			if (chooser.showOpenDialog(primaryFrame) == JFileChooser.APPROVE_OPTION) {
				var file = chooser.getSelectedFile();
				try (var decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)))) {
					preset = (Preset) decoder.readObject();
					updateOptions();
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private final class PickupAction extends AbstractAction {
		private static final long serialVersionUID = 3506420106141972667L;

		private PickupAction() {
			super(FX.PICKUP.toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng, FX.PICKUP);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class PlayAction extends AbstractAction {
		private static final long serialVersionUID = -246651351683718566L;

		PlayAction() {
			super("play");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class PowerupAction extends AbstractAction {
		private static final long serialVersionUID = 6739865468115456157L;

		private PowerupAction() {
			super(FX.POWERUP.toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng, FX.POWERUP);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class RandomAction extends AbstractAction {
		private static final long serialVersionUID = -246651351683718566L;

		RandomAction() {
			super("ramdom");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			preset.random(rng);
			updateOptions();
			updateVisualizer();
			Application.this.play(preset);
		}
	}

	private final class SaveAction extends AbstractAction {
		private static final long serialVersionUID = -246651351683718566L;

		SaveAction() {
			super("save");
			putValue(SHORT_DESCRIPTION, "save sfp file");
			putValue(MNEMONIC_KEY, KeyEvent.VK_A);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			logger.log(Level.TRACE, "SaveAction#actionPerformed");

			var chooser = new JFileChooser();
			var filter = new FileNameExtensionFilter("sfp file", "sfp");
			chooser.setFileFilter(filter);
			if (chooser.showSaveDialog(primaryFrame) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				String path = file.getPath();
				if (!path.toLowerCase().endsWith(".sfp")) {
					file = new File(path + ".sfp");
				}

				try (var encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8", false,
						1)) {
					encoder.writeObject(preset);
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private final class SliderContainer extends JPanel implements javax.swing.Scrollable {
		private static final long serialVersionUID = 5361232438017420043L;

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return getScrollableUnitIncrement(visibleRect, orientation, direction);
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			// XXX: SwingBuilderにスライダーパネルの初期化順を破壊されるのでパネルの順番にアクセスする手段がない
			// XXX: 全てのパネルが同じサイズになるはずなのでstartFrequencyPanelの高さを返す
			/*
			 * JPanel[] slidersContainerChildren = new JPanel[]{ startFrequencyPanel,
			 * lpFilterResonancePanel,squareDutyPanel, slidePanel, deltaSlidePanel,
			 * dutySweepPanel, attackPanel, vibratoStrengthPanel, vibratoSpeedPanel,
			 * sustainPanel, punchPanel, decayPanel, hpFilterCutoffPanel,
			 * lpFilterSweepPanel, lpFilterCutoffPanel, hpFilterSweepPanel,
			 * changeSpeedPanel, repeatSpeedPanel, phaserSweepPanel, phaserOffsetPanel,
			 * changeAmountPanel }; for(int i = 0; i < slidersContainerChildren.length;
			 * i++){ var childBounds = slidersContainerChildren[i].getBounds(); if
			 * (childBounds.intersects(visibleRect)) { return childBounds.height; } }
			 */
			return startFrequencyPanel.getHeight();
		}
	}

	private final class ThemeAction<T extends Supplier<? extends MetalTheme> & Serializable> extends AbstractAction {
		private static final long serialVersionUID = -3313746490480918100L;

		private final T themeSupplier;

		ThemeAction(T themeSupplier, String name) {
			super(name);
			this.themeSupplier = themeSupplier;
			putValue(SHORT_DESCRIPTION, "change metal theme to " + name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			logger.log(Level.TRACE, "ThemeAction#actionPerformed");

			MetalLookAndFeel.setCurrentTheme(themeSupplier.get());
			try {
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			} catch (UnsupportedLookAndFeelException ex) {
				ex.printStackTrace();
			}
			updateComponentTreeUI(primaryFrame);
		}
	}

	private static final System.Logger logger = System.getLogger(Application.class.getName());

	private static final Font DEFAULT_USER_SELECTED_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	public static void main(String... args) {
		SwingUtilities.invokeLater(Application::new);
	}

	private final int SLIDER_PRECISION = 10000;

	private final float FONT_SCALE = 1.25f;

	private final RandomGenerator.SplittableGenerator rng;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Preset preset;

	private JFrame primaryFrame;

	private JSlider volumeSlider;

	private JLabel volumeLabel;
	private JButton playButton;
	private JButton randomButton;
	private JButton mutateButton;

	private JButton pickupButton;
	private JButton laserButton;
	private JButton explosionButton;
	private JButton powerupButton;
	private JButton hurtButton;
	private JButton jumpButton;
	private JButton beepButton;

	private JComboBox<WaveForm> waveFormComboBox;

	private SoundVisualizer soundVisualizer;
	private JSeparator buttonTopSeparator;

	private JSlider startFrequencySlider;

	private JLabel startFrequencyValueLabel;

	private JSlider slideSlider;

	private JLabel slideValueLabel;

	private JSlider deltaSlideSlider;

	private JLabel deltaSlideValueLabel;

	private JSlider squareDutySlider;

	private JLabel squareDutyValueLabel;

	private JSlider dutySweepSlider;

	private JLabel dutySweepValueLabel;

	private JSlider vibratoStrengthSlider;

	private JLabel vibratoStrengthValueLabel;

	private JSlider vibratoSpeedSlider;

	private JLabel vibratoSpeedValueLabel;

	private JSlider attackSlider;

	private JLabel attackValueLabel;

	private JSlider sustainSlider;

	private JLabel sustainValueLabel;

	private JSlider decaySlider;

	private JLabel decayValueLabel;

	private JSlider punchSlider;

	private JLabel punchValueLabel;

	private JSlider lpFilterResonanceSlider;

	private JLabel lpFilterResonanceValueLabel;

	private JSlider lpFilterCutoffSlider;

	private JLabel lpFilterCutoffValueLabel;

	private JSlider lpFilterSweepSlider;

	private JLabel lpFilterSweepValueLabel;

	private JSlider hpFilterCutoffSlider;

	private JLabel hpFilterCutoffValueLabel;

	private JSlider hpFilterSweepSlider;

	private JLabel hpFilterSweepValueLabel;

	private JSlider phaserOffsetSlider;

	private JLabel phaserOffsetValueLabel;

	private JSlider phaserSweepSlider;

	private JLabel phaserSweepValueLabel;

	private JSlider repeatSpeedSlider;

	private JLabel repeatSpeedValueLabel;

	private JSlider changeAmountSlider;

	private JLabel changeAmountValueLabel;

	private JSlider changeSpeedSlider;

	private JLabel changeSpeedValueLabel;

	private JPanel startFrequencyPanel;

	private JPanel slidePanel;

	private JPanel deltaSlidePanel;

	private JPanel squareDutyPanel;

	private JPanel dutySweepPanel;

	private JPanel vibratoStrengthPanel;

	private JPanel vibratoSpeedPanel;

	private JPanel attackPanel;

	private JPanel sustainPanel;

	private JPanel decayPanel;

	private JPanel punchPanel;

	private JPanel lpFilterResonancePanel;

	private JPanel lpFilterCutoffPanel;

	private JPanel lpFilterSweepPanel;

	private JPanel hpFilterCutoffPanel;

	private JPanel hpFilterSweepPanel;

	private JPanel phaserOffsetPanel;

	private JPanel phaserSweepPanel;

	private JPanel repeatSpeedPanel;

	private JPanel changeAmountPanel;

	private JPanel changeSpeedPanel;

	private JSeparator buttonButtomSeparator;

	private ActionMap globalActionMap = new ActionMap();
	private ComponentInputMap globalInputMap;

	private Optional<Font> selectedFont = Optional.empty();

	/**
	 * @wbp.parser.entryPoint
	 */
	public Application() {
//		System.setProperty("awt.useSystemAAFontSettings", "on");
		// 標準のLAFを使用しシステムフォントを使用しない際、
		// LAFが設定するフォントサイズを大きくする
		Optional.ofNullable(UIManager.getLookAndFeel())
				.filter(laf -> laf.getClass().getName().contains("javax.swing.plaf")).ifPresent(laf -> {
					if (!useSystemFonts()) {
						resizeStanderdLookAndFeelFont(laf);
					}
				});

		var randomGeneratorFactory = RandomGeneratorFactory.of("L64X1024MixRandom");
		var seed = (new SecureRandom()).generateSeed(randomGeneratorFactory.stateBits());
		rng = (SplittableGenerator) randomGeneratorFactory.create(seed);

		primaryFrame = new JFrame();
		primaryFrame.setTitle("SFXR**");
		primaryFrame.setResizable(true);
		primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		preset = Preset.fromFx(rng, FX.BEEP);
		preset.random(rng);

		initContentPane();

		updateOptions();

		primaryFrame.pack();
		primaryFrame.setVisible(true);
	}

	private JPanel createAttackPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("attack"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleAttackSliderStateChanged);

		attackSlider = new JSlider(model);
		attackSlider.setMajorTickSpacing(model.getMaximum());
		attackSlider.setPaintTicks(true);

		attackValueLabel = new JLabel();
		attackValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(attackSlider);
		panel.add(attackValueLabel);
		return panel;
	}

	private JPanel createButtonContainer() {
		var buttonsContainer = new JPanel();
		buttonsContainer.setLayout(new GridLayout(0, 1, 0, 0));
		buttonsContainer.setBorder(createEmptyBorder(8, 4, 0, 4));

		pickupButton = new JButton(new PickupAction());
		laserButton = new JButton(new LaserAction());
		explosionButton = new JButton(new ExplosionAction());
		powerupButton = new JButton(new PowerupAction());
		hurtButton = new JButton(new HurtAction());
		jumpButton = new JButton(new JumpAction());
		beepButton = new JButton(new BeepAction());

		buttonTopSeparator = new JSeparator();
		randomButton = new JButton(new RandomAction());
		mutateButton = new JButton(new MutateAction());
		playButton = new JButton(new PlayAction());
		buttonButtomSeparator = new JSeparator();

		buttonsContainer.add(pickupButton);
		buttonsContainer.add(laserButton);
		buttonsContainer.add(explosionButton);
		buttonsContainer.add(powerupButton);
		buttonsContainer.add(hurtButton);
		buttonsContainer.add(jumpButton);
		buttonsContainer.add(beepButton);

		buttonsContainer.add(buttonTopSeparator);
		buttonsContainer.add(randomButton);
		buttonsContainer.add(mutateButton);
		buttonsContainer.add(playButton);
		buttonsContainer.add(buttonButtomSeparator);

		return buttonsContainer;
	}

	private JPanel createChangeAmountPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("change amount"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleChangeAmountSliderStateChanged);

		changeAmountSlider = new JSlider(model);
		changeAmountSlider.setMajorTickSpacing(model.getMaximum());
		changeAmountSlider.setPaintTicks(true);

		changeAmountValueLabel = new JLabel();
		changeAmountValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(changeAmountSlider);
		panel.add(changeAmountValueLabel);
		return panel;
	}

	private JPanel createChangeSpeedPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("change speed"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleChangeSpeedSliderStateChanged);

		changeSpeedSlider = new JSlider(model);
		changeSpeedSlider.setMajorTickSpacing(model.getMaximum());
		changeSpeedSlider.setPaintTicks(true);

		changeSpeedValueLabel = new JLabel();
		changeSpeedValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(changeSpeedSlider);
		panel.add(changeSpeedValueLabel);
		return panel;
	}

	private JPanel createDecayPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("decay"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleDecaySliderStateChanged);

		decaySlider = new JSlider(model);
		decaySlider.setMajorTickSpacing(model.getMaximum());
		decaySlider.setPaintTicks(true);

		decayValueLabel = new JLabel();
		decayValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(decaySlider);
		panel.add(decayValueLabel);
		return panel;
	}

	private JPanel createDeltaSlidePanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("delta slide"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, -SLIDER_PRECISION / 2, SLIDER_PRECISION / 2);
		model.addChangeListener(this::handleDeltaSlideSliderStateChanged);

		deltaSlideSlider = new JSlider(model);
		deltaSlideSlider.setMajorTickSpacing(model.getMaximum());
		deltaSlideSlider.setPaintTicks(true);

		deltaSlideValueLabel = new JLabel();
		deltaSlideValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(deltaSlideSlider);
		panel.add(deltaSlideValueLabel);
		return panel;
	}

	private JPanel createDutySweepPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("duty sweep"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, -SLIDER_PRECISION / 2, SLIDER_PRECISION / 2);
		model.addChangeListener(this::handleDutySweepSliderStateChanged);

		dutySweepSlider = new JSlider(model);
		dutySweepSlider.setMajorTickSpacing(model.getMaximum());
		dutySweepSlider.setPaintTicks(true);

		dutySweepValueLabel = new JLabel();
		dutySweepValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(dutySweepSlider);
		panel.add(dutySweepValueLabel);
		return panel;
	}

	private JPanel createHpFilterCutoffPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("HP filter cutoff"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleHpFilterCutoffSliderStateChanged);

		hpFilterCutoffSlider = new JSlider(model);
		hpFilterCutoffSlider.setMajorTickSpacing(model.getMaximum());
		hpFilterCutoffSlider.setPaintTicks(true);

		hpFilterCutoffValueLabel = new JLabel();
		hpFilterCutoffValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(hpFilterCutoffSlider);
		panel.add(hpFilterCutoffValueLabel);
		return panel;
	}

	private JPanel createHpFilterSweepPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("HP filter sweep"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, -SLIDER_PRECISION / 2, SLIDER_PRECISION / 2);
		model.addChangeListener(this::handleHpFilterSweepSliderStateChanged);

		hpFilterSweepSlider = new JSlider(model);
		hpFilterSweepSlider.setMajorTickSpacing(model.getMaximum());
		hpFilterSweepSlider.setPaintTicks(true);

		hpFilterSweepValueLabel = new JLabel();
		hpFilterSweepValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(hpFilterSweepSlider);
		panel.add(hpFilterSweepValueLabel);
		return panel;
	}

	private JPanel createLpFilterCutoffPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("LP filter cutoff"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleLpFilterCutoffSliderStateChanged);

		lpFilterCutoffSlider = new JSlider(model);
		lpFilterCutoffSlider.setMajorTickSpacing(model.getMaximum());
		lpFilterCutoffSlider.setPaintTicks(true);

		lpFilterCutoffValueLabel = new JLabel();
		lpFilterCutoffValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(lpFilterCutoffSlider);
		panel.add(lpFilterCutoffValueLabel);
		return panel;
	}

	private JPanel createLpFilterResonancePanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("LP filter resonance"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleLpFilterResonanceSliderStateChanged);

		lpFilterResonanceSlider = new JSlider(model);
		lpFilterResonanceSlider.setMajorTickSpacing(model.getMaximum());
		lpFilterResonanceSlider.setPaintTicks(true);

		lpFilterResonanceValueLabel = new JLabel();
		lpFilterResonanceValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(lpFilterResonanceSlider);
		panel.add(lpFilterResonanceValueLabel);

		return panel;
	}

	private JPanel createLpFilterSweepPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("LP filter sweep"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, -SLIDER_PRECISION / 2, SLIDER_PRECISION / 2);
		model.addChangeListener(this::handleLpFilterSweepSliderStateChanged);

		lpFilterSweepSlider = new JSlider(model);
		lpFilterSweepSlider.setMajorTickSpacing(model.getMaximum());
		lpFilterSweepSlider.setPaintTicks(true);

		lpFilterSweepValueLabel = new JLabel();
		lpFilterSweepValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(lpFilterSweepSlider);
		panel.add(lpFilterSweepValueLabel);
		return panel;
	}

	private JMenuBar createMenuBar() {
		var saveMenuItem = new JMenuItem(globalActionMap.get("save"));
		var openMenuItem = new JMenuItem(globalActionMap.get("open"));
		var fontMenuItem = new JMenuItem(new FontAction());
		var exportMenuItem = new JMenuItem(globalActionMap.get("export"));

		var fileMenu = new JMenu(new FileMenuAction());

		fileMenu.add(saveMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(exportMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(fontMenuItem);

		var themeMenu = new JMenu(new LookAndFeelMenuAction());

		if (UIManager.getLookAndFeel().getName().contains("Metal")) {

			var oceanItem = new JMenuItem(
					new ThemeAction<>((Supplier<OceanTheme> & Serializable) OceanTheme::new, "ocean"));
			themeMenu.add(oceanItem);

			var steeleItem = new JMenuItem(
					new ThemeAction<>((Supplier<DefaultMetalTheme> & Serializable) DefaultMetalTheme::new, "steele"));
			themeMenu.add(steeleItem);

			var invertItem = new JMenuItem(
					new ThemeAction<>((Supplier<InvertTheme> & Serializable) InvertTheme::new, "invert"));
			themeMenu.add(invertItem);

			var silverItem = new JMenuItem(
					new ThemeAction<>((Supplier<SilverTheme> & Serializable) SilverTheme::new, "silver"));
			themeMenu.add(silverItem);

			var ironItem = new JMenuItem(
					new ThemeAction<>((Supplier<IronTheme> & Serializable) IronTheme::new, "iron"));
			themeMenu.add(ironItem);

			var oldschoolItem = new JMenuItem(
					new ThemeAction<>((Supplier<OldschoolTheme> & Serializable) OldschoolTheme::new, "oldschool"));
			themeMenu.add(oldschoolItem);

			var matchaItem = new JMenuItem(
					new ThemeAction<>((Supplier<MatchaTheme> & Serializable) MatchaTheme::new, "matcha"));
			themeMenu.add(matchaItem);
		}

		var menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(themeMenu);
		return menuBar;
	}

	private JPanel createPhaserOffsetPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("phaser offset"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, -SLIDER_PRECISION / 2, SLIDER_PRECISION / 2);
		model.addChangeListener(this::handlePhaserOffsetSliderStateChanged);

		phaserOffsetSlider = new JSlider(model);
		phaserOffsetSlider.setMajorTickSpacing(model.getMaximum());
		phaserOffsetSlider.setPaintTicks(true);

		phaserOffsetValueLabel = new JLabel();
		phaserOffsetValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(phaserOffsetSlider);
		panel.add(phaserOffsetValueLabel);
		return panel;
	}

	private JPanel createPhaserSweepPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("phaser sweep"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, -SLIDER_PRECISION / 2, SLIDER_PRECISION / 2);
		model.addChangeListener(this::handlePhaserSweepSliderStateChanged);

		phaserSweepSlider = new JSlider(model);
		phaserSweepSlider.setMajorTickSpacing(model.getMaximum());
		phaserSweepSlider.setPaintTicks(true);

		phaserSweepValueLabel = new JLabel();
		phaserSweepValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(phaserSweepSlider);
		panel.add(phaserSweepValueLabel);
		return panel;
	}

	private JPanel createPunchPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("punch"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handlePunchSliderStateChanged);

		punchSlider = new JSlider(model);
		punchSlider.setMajorTickSpacing(model.getMaximum());
		punchSlider.setPaintTicks(true);

		punchValueLabel = new JLabel();
		punchValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(punchSlider);
		panel.add(punchValueLabel);
		return panel;
	}

	private JPanel createRepeatSpeedPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("repeat speed"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleRepeatSpeedSliderStateChanged);

		repeatSpeedSlider = new JSlider(model);
		repeatSpeedSlider.setMajorTickSpacing(model.getMaximum());
		repeatSpeedSlider.setPaintTicks(true);

		repeatSpeedValueLabel = new JLabel();
		repeatSpeedValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(repeatSpeedSlider);
		panel.add(repeatSpeedValueLabel);
		return panel;
	}

	private JPanel createSlidePanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("slide"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, -SLIDER_PRECISION / 2, SLIDER_PRECISION / 2);
		model.addChangeListener(this::handleSlideSliderStateChanged);

		slideSlider = new JSlider(model);
		slideSlider.setMajorTickSpacing(model.getMaximum());
		slideSlider.setPaintTicks(true);

		slideValueLabel = new JLabel();
		slideValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(slideSlider);
		panel.add(slideValueLabel);
		return panel;
	}

	private JScrollPane createSliderScrollPane() {
		lpFilterResonancePanel = createLpFilterResonancePanel();
		squareDutyPanel = createSquareDutyPanel();
		slidePanel = createSlidePanel();
		startFrequencyPanel = createStartFrequencyPanel();
		deltaSlidePanel = createDeltaSlidePanel();
		dutySweepPanel = createDutySweepPanel();
		attackPanel = createAttackPanel();
		vibratoStrengthPanel = createVibratoStrengthPanel();
		vibratoSpeedPanel = createVibratoSpeedPanel();
		sustainPanel = createSustainPanel();
		punchPanel = createPunchPanel();
		decayPanel = createDecayPanel();
		hpFilterCutoffPanel = createHpFilterCutoffPanel();
		hpFilterSweepPanel = createHpFilterSweepPanel();
		lpFilterCutoffPanel = createLpFilterCutoffPanel();
		lpFilterSweepPanel = createLpFilterSweepPanel();
		changeSpeedPanel = createChangeSpeedPanel();
		repeatSpeedPanel = createRepeatSpeedPanel();
		phaserSweepPanel = createPhaserSweepPanel();
		phaserOffsetPanel = createPhaserOffsetPanel();
		changeAmountPanel = createChangeAmountPanel();

		var sliderContainer = new SliderContainer();
		sliderContainer.setLayout(new BoxLayout(sliderContainer, BoxLayout.PAGE_AXIS));
		sliderContainer.setBorder(createEmptyBorder(0, 4, 0, 4));

		sliderContainer.add(startFrequencyPanel);
		sliderContainer.add(slidePanel);
		sliderContainer.add(deltaSlidePanel);
		sliderContainer.add(squareDutyPanel);
		sliderContainer.add(dutySweepPanel);
		sliderContainer.add(vibratoStrengthPanel);
		sliderContainer.add(vibratoSpeedPanel);
		sliderContainer.add(attackPanel);
		sliderContainer.add(sustainPanel);
		sliderContainer.add(decayPanel);
		sliderContainer.add(punchPanel);
		sliderContainer.add(lpFilterResonancePanel);
		sliderContainer.add(lpFilterCutoffPanel);
		sliderContainer.add(lpFilterSweepPanel);
		sliderContainer.add(hpFilterCutoffPanel);
		sliderContainer.add(hpFilterSweepPanel);
		sliderContainer.add(phaserOffsetPanel);
		sliderContainer.add(phaserSweepPanel);
		sliderContainer.add(repeatSpeedPanel);
		sliderContainer.add(changeAmountPanel);
		sliderContainer.add(changeSpeedPanel);

		var sliderScrollPane = new JScrollPane(sliderContainer, VERTICAL_SCROLLBAR_AS_NEEDED,
				HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sliderScrollPane.setBorder(createEmptyBorder(8, 0, 8, 0));
		return sliderScrollPane;
	}

	private JPanel createSquareDutyPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("square duty"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleSquareDutySliderStateChanged);
		squareDutySlider = new JSlider(model);
		squareDutySlider.setMajorTickSpacing(model.getMaximum());
		squareDutySlider.setPaintTicks(true);

		squareDutyValueLabel = new JLabel();
		squareDutyValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(squareDutySlider);
		panel.add(squareDutyValueLabel);
		return panel;
	}

	private JPanel createStartFrequencyPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("start frequency"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleStartFrequencySliderStateChanged);
		startFrequencySlider = new JSlider(model);
		startFrequencySlider.setMajorTickSpacing(model.getMaximum());
		startFrequencySlider.setPaintTicks(true);

		startFrequencyValueLabel = new JLabel();
		startFrequencyValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(startFrequencySlider);
		panel.add(startFrequencyValueLabel);
		return panel;
	}

	private JPanel createSustainPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("sustain"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleSustainSliderStateChanged);

		sustainSlider = new JSlider(model);
		sustainSlider.setMajorTickSpacing(model.getMaximum());
		sustainSlider.setPaintTicks(true);

		sustainValueLabel = new JLabel();
		sustainValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(sustainSlider);
		panel.add(sustainValueLabel);
		return panel;
	}

	private JPanel createTopContainer() {
		var topContainer = new JPanel();
		topContainer.setBorder(createEmptyBorder(8, 4, 0, 4));

		GridBagLayout gbl_topContainer = new GridBagLayout();
		gbl_topContainer.columnWidths = new int[] { 50, 200, 50, 300, 100, 0 };
		gbl_topContainer.rowHeights = new int[] { 40 };
		gbl_topContainer.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_topContainer.rowWeights = new double[] { 1.0 };
		topContainer.setLayout(gbl_topContainer);

		var waveFormLabel = new JLabel("wave form");
		GridBagConstraints gbc_waveFormLabel = new GridBagConstraints();
		gbc_waveFormLabel.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
		gbc_waveFormLabel.insets = new Insets(0, 0, 0, 5);
		gbc_waveFormLabel.gridx = 0;
		gbc_waveFormLabel.gridy = 0;
		topContainer.add(waveFormLabel, gbc_waveFormLabel);

		var waveFromModel = new DefaultComboBoxModel<WaveForm>(WaveForm.values());
		waveFormComboBox = new JComboBox<WaveForm>(waveFromModel);
		waveFormComboBox.setPreferredSize(new Dimension(300, 30));
		waveFormComboBox.addItemListener(this::handleWaveFormComboBoxItemStateChanged);

		GridBagConstraints gbc_waveFormComboBox = new GridBagConstraints();
		gbc_waveFormComboBox.anchor = GridBagConstraints.WEST;
		gbc_waveFormComboBox.fill = GridBagConstraints.VERTICAL;
		gbc_waveFormComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_waveFormComboBox.gridx = 1;
		gbc_waveFormComboBox.gridy = 0;
		topContainer.add(waveFormComboBox, gbc_waveFormComboBox);

		JLabel label = new JLabel("master volume");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
		gbc_label.insets = new Insets(0, 0, 0, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 0;
		topContainer.add(label, gbc_label);

		volumeSlider = new JSlider(0, SLIDER_PRECISION);
		volumeSlider.getModel().addChangeListener(this::handleVolumeSliderStateChanged);
		GridBagConstraints gbc_volumeSlider = new GridBagConstraints();
		gbc_volumeSlider.fill = GridBagConstraints.BOTH;
		gbc_volumeSlider.insets = new Insets(0, 0, 0, 5);
		gbc_volumeSlider.gridx = 3;
		gbc_volumeSlider.gridy = 0;
		topContainer.add(volumeSlider, gbc_volumeSlider);

		volumeLabel = new JLabel();
		GridBagConstraints gbc_volumeLabel = new GridBagConstraints();
		gbc_volumeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_volumeLabel.anchor = GridBagConstraints.ABOVE_BASELINE;
		gbc_volumeLabel.gridx = 4;
		gbc_volumeLabel.gridy = 0;
		topContainer.add(volumeLabel, gbc_volumeLabel);

		return topContainer;
	}

	private JPanel createVibratoSpeedPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("vibrato speed"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleVibratoSpeedSliderStateChanged);

		vibratoSpeedSlider = new JSlider(model);
		vibratoSpeedSlider.setMajorTickSpacing(model.getMaximum());
		vibratoSpeedSlider.setPaintTicks(true);

		vibratoSpeedValueLabel = new JLabel();
		vibratoSpeedValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(vibratoSpeedSlider);
		panel.add(vibratoSpeedValueLabel);
		return panel;
	}

	private JPanel createVibratoStrengthPanel() {
		var panel = new JPanel();
		panel.setBorder(new TitledBorder("vibrato strength"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		var model = new DefaultBoundedRangeModel(0, 0, 0, SLIDER_PRECISION);
		model.addChangeListener(this::handleVibratoStrengthSliderStateChanged);

		vibratoStrengthSlider = new JSlider(model);
		vibratoStrengthSlider.setMajorTickSpacing(model.getMaximum());
		vibratoStrengthSlider.setPaintTicks(true);

		vibratoStrengthValueLabel = new JLabel();
		vibratoStrengthValueLabel.setText(String.valueOf(model.getValue()));

		panel.add(vibratoStrengthSlider);
		panel.add(vibratoStrengthValueLabel);

		return panel;
	}

	private void disableMenuBar() {
		var menuBar = primaryFrame.getJMenuBar();
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			menuBar.getMenu(i).setEnabled(false);
		}
	}

	private void enableMenuBar() {
		var menuBar = primaryFrame.getJMenuBar();
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			menuBar.getMenu(i).setEnabled(true);
		}
	}

	private void handleAttackSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			attackValueLabel.setText(String.valueOf(value));
			preset.setAttackTime(value);
		}
	}

	private void handleChangeAmountSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			changeAmountValueLabel.setText(String.valueOf(value));
			preset.setArpeggioDepth(value);
		}
	}

	private void handleChangeSpeedSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			changeSpeedValueLabel.setText(String.valueOf(value));
			preset.setArpeggioSpeed(value);
		}
	}

	private void handleDecaySliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			decayValueLabel.setText(String.valueOf(value));
			preset.setDecayTime(value);
		}
	}

	private void handleDeltaSlideSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) (SLIDER_PRECISION / 2);
			deltaSlideValueLabel.setText(String.valueOf(value));
			preset.setPitchDeltaSlide(value);
		}
	}

	private void handleDutySweepSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) (SLIDER_PRECISION / 2);
			dutySweepValueLabel.setText(String.valueOf(value));
			preset.setSquareDutySlide(value);
		}
	}

	private void handleHpFilterCutoffSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			hpFilterCutoffValueLabel.setText(String.valueOf(value));
			preset.setHighpassFilterCutoff(value);
		}
	}

	private void handleHpFilterSweepSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) (SLIDER_PRECISION / 2);
			hpFilterSweepValueLabel.setText(String.valueOf(value));
			preset.setHighpassFilterCutoffSlide(value);
		}
	}

	private void handleLpFilterCutoffSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			lpFilterCutoffValueLabel.setText(String.valueOf(value));
			preset.setLowpassFilterCutoff(value);
		}
	}

	private void handleLpFilterResonanceSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			lpFilterResonanceValueLabel.setText(String.valueOf(value));
			preset.setLowpassFilterResonance(value);
		}
	}

	private void handleLpFilterSweepSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) (SLIDER_PRECISION / 2);
			lpFilterSweepValueLabel.setText(String.valueOf(value));
			preset.setLowpassFilterCutoffSlide(value);
		}
	}

	private void handlePhaserOffsetSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) (SLIDER_PRECISION / 2);
			phaserOffsetValueLabel.setText(String.valueOf(value));
			preset.setPhaserOffset(value);
		}
	}

	private void handlePhaserSweepSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) (SLIDER_PRECISION / 2);
			phaserSweepValueLabel.setText(String.valueOf(value));
			preset.setPhaserSlide(value);
		}
	}

	private void handlePunchSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			punchValueLabel.setText(String.valueOf(value));
			preset.setSustainPunch(value);
		}
	}

	private void handleRepeatSpeedSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			repeatSpeedValueLabel.setText(String.valueOf(value));
			preset.setRepeatSpeed(value);
		}
	}

	private void handleSlideSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION / 2;
			slideValueLabel.setText(String.valueOf(value));
			preset.setPitchSlide(value);
		}
	}

	private void handleSquareDutySliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			squareDutyValueLabel.setText(String.valueOf(value));
			preset.setSquareDuty(value);
		}
	}

	private void handleStartFrequencySliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			startFrequencyValueLabel.setText(String.valueOf(value));
			preset.setStartFrequency(value);
		}
	}

	private void handleSustainSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			sustainValueLabel.setText(String.valueOf(value));
			preset.setSustainTime(value);
		}
	}

	private void handleVibratoSpeedSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			vibratoSpeedValueLabel.setText(String.valueOf(value));
			preset.setVibratoSpeed(value);
		}
	}

	private void handleVibratoStrengthSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			vibratoStrengthValueLabel.setText(String.valueOf(value));
			preset.setVibratoStrength(value);
		}
	}

	private void handleVolumeSliderStateChanged(ChangeEvent e) {
		if (e.getSource() instanceof BoundedRangeModel model) {
			float value = model.getValue() / (float) SLIDER_PRECISION;
			volumeLabel.setText(String.valueOf(value));
			preset.setMasterVolume(value);
		}
	}

	private void handleWaveFormComboBoxItemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			preset.setWaveType((WaveForm) e.getItem());
		}
	}

	private void initContentPane() {
		primaryFrame.getContentPane().setLayout(new BorderLayout());
		primaryFrame.getContentPane().setPreferredSize(new Dimension(1280, 768));

		var buttonContainer = createButtonContainer();

		var topContainer = createTopContainer();

		var uiActionMap = getUIActionMap(topContainer);
		if (nonNull(uiActionMap)) {
			globalActionMap.setParent(uiActionMap);
		}

		var exportAction = new ExportAction();
		globalActionMap.put(exportAction.getValue(Action.NAME), exportAction);

		var saveAction = new SaveAction();
		globalActionMap.put(saveAction.getValue(Action.NAME), saveAction);

		var openAction = new OpenAction();
		globalActionMap.put(openAction.getValue(Action.NAME), openAction);

		globalInputMap = new ComponentInputMap(topContainer);

		var uiInputMap = getUIInputMap(topContainer, WHEN_IN_FOCUSED_WINDOW);
		if (nonNull(uiInputMap)) {
			globalInputMap.setParent(uiInputMap);
		}

		var exportModifiers = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
		var exportKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, exportModifiers);

		globalInputMap.put(exportKeyStroke, exportAction.getValue(Action.NAME));

		var saveModifiers = InputEvent.CTRL_DOWN_MASK;
		var saveKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, saveModifiers);

		globalInputMap.put(saveKeyStroke, saveAction.getValue(Action.NAME));

		var openModifiers = InputEvent.CTRL_DOWN_MASK;
		var openKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, openModifiers);

		globalInputMap.put(openKeyStroke, openAction.getValue(Action.NAME));

		topContainer.setActionMap(globalActionMap);
		topContainer.setInputMap(WHEN_IN_FOCUSED_WINDOW, globalInputMap);

		var sliderScrollPane = createSliderScrollPane();

		soundVisualizer = new SoundVisualizer();
		soundVisualizer.setPreferredSize(new Dimension(300, 300));
		soundVisualizer.setBorder(
				createCompoundBorder(createEmptyBorder(0, 4, 8, 4), createEtchedBorder(EtchedBorder.LOWERED)));

		primaryFrame.getContentPane().add(BorderLayout.WEST, buttonContainer);
		primaryFrame.getContentPane().add(BorderLayout.NORTH, topContainer);
		primaryFrame.getContentPane().add(BorderLayout.CENTER, sliderScrollPane);
		primaryFrame.getContentPane().add(BorderLayout.SOUTH, soundVisualizer);

		var menuBar = createMenuBar();
		primaryFrame.setJMenuBar(menuBar);
	}

	/**
	 * A little wrapper to easier the usage of a Preset in a game
	 *
	 * @param preset
	 */
	private void play(Preset preset) {
		var syn = new Synth(preset, rng);
		var sound = syn.createSound(rng);
		try {
			var soundThread = (Sound) sound.clone();
			executor.execute(() -> playImpl(soundThread));
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
			return;
		}
	}

	private void playImpl(Sound sound) {
		var audioFormat = new AudioFormat(sound.getSampleRate(), 8, 1, true, true);
		try (var line = AudioSystem.getSourceDataLine(audioFormat)) {
			line.open(audioFormat);
			line.start();
			line.write(sound.getPcm(), 0, sound.getPcm().length);
			line.drain();
		} catch (LineUnavailableException e) {
			logger.log(Level.ERROR, e);
		}
	}

	private void resizeStanderdLookAndFeelFont(LookAndFeel lookAndFeel) {
//         UIManager.put("swing.boldMetal", Boolean.FALSE);
		var defaults = UIManager.getLookAndFeelDefaults();
		defaults.forEach((key, value) -> {
			if (key.toString().toLowerCase().contains("font")) {
				Object replaced = switch (value) {
				case UIDefaults.LazyValue _ -> {
					var font = (Font) defaults.get(key);
					logger.log(Level.INFO, key + ": " + font);
					float size = font.getSize2D() * FONT_SCALE;
					var derivedFont = font.deriveFont(size);
					yield (UIDefaults.LazyValue) _ -> new FontUIResource(derivedFont);
				}
				case UIDefaults.ActiveValue _ -> {
					var font = (Font) defaults.get(key);
					logger.log(Level.INFO, key + ": " + font);
					float size = font.getSize2D() * FONT_SCALE;
					var derivedFont = font.deriveFont(size);
					yield (UIDefaults.ActiveValue) _ -> new FontUIResource(derivedFont);
				}
				case Font font -> {
					logger.log(Level.INFO, key + ": " + font);
					float size = font.getSize2D() * FONT_SCALE;
					var derivedFont = font.deriveFont(size);
					yield new FontUIResource(derivedFont);
				}
				default -> throw new AssertionError();
				};
				UIManager.put(key, replaced);
			}
		});
	}

	private void resizeStanderdLookAndFeelFont(LookAndFeel lookAndFeel, Font selectedFont) {
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		var defaults = UIManager.getLookAndFeelDefaults();
		defaults.forEach((key, value) -> {
			if (key.toString().toLowerCase().contains("font")) {
				Object replaced = switch (value) {
				case UIDefaults.LazyValue _ -> {
					yield (UIDefaults.LazyValue) _ -> new FontUIResource(selectedFont);
				}
				case UIDefaults.ActiveValue _ -> {
					yield (UIDefaults.ActiveValue) _ -> new FontUIResource(selectedFont);
				}
				case Font font -> {
					logger.log(Level.INFO, key + ": " + font);
					yield new FontUIResource(selectedFont);
				}
				default -> throw new AssertionError();
				};
				UIManager.put(key, replaced);
			}
		});
	}

	public void updateOptions() {
		waveFormComboBox.setSelectedItem(preset.getWaveType());

		startFrequencySlider.setValue((int) (preset.getStartFrequency() * SLIDER_PRECISION));
		slideSlider.setValue((int) (preset.getPitchSlide() * SLIDER_PRECISION / 2));
		deltaSlideSlider.setValue((int) (preset.getPitchDeltaSlide() * SLIDER_PRECISION / 2));
		squareDutySlider.setValue((int) (preset.getSquareDuty() * SLIDER_PRECISION));
		dutySweepSlider.setValue((int) (preset.getSquareDutySlide() * SLIDER_PRECISION / 2));

		vibratoStrengthSlider.setValue((int) (preset.getSquareDuty() * SLIDER_PRECISION));
		vibratoSpeedSlider.setValue((int) (preset.getSquareDuty() * SLIDER_PRECISION));

		attackSlider.setValue((int) (preset.getAttackTime() * SLIDER_PRECISION));
		sustainSlider.setValue((int) (preset.getSustainTime() * SLIDER_PRECISION));
		decaySlider.setValue((int) (preset.getDecayTime() * SLIDER_PRECISION));
		punchSlider.setValue((int) (preset.getSustainPunch() * SLIDER_PRECISION));

		lpFilterResonanceSlider.setValue((int) (preset.getLowpassFilterResonance() * SLIDER_PRECISION));
		lpFilterCutoffSlider.setValue((int) (preset.getLowpassFilterCutoff() * SLIDER_PRECISION));
		lpFilterSweepSlider.setValue((int) (preset.getLowpassFilterCutoffSlide() * SLIDER_PRECISION / 2));
		hpFilterCutoffSlider.setValue((int) (preset.getHighpassFilterCutoff() * SLIDER_PRECISION));
		hpFilterSweepSlider.setValue((int) (preset.getHighpassFilterCutoffSlide() * SLIDER_PRECISION / 2));

		phaserOffsetSlider.setValue((int) (preset.getPhaserOffset() * SLIDER_PRECISION / 2));
		phaserSweepSlider.setValue((int) (preset.getPhaserSlide() * SLIDER_PRECISION / 2));

		repeatSpeedSlider.setValue((int) (preset.getRepeatSpeed() * SLIDER_PRECISION));

		changeAmountSlider.setValue((int) (preset.getArpeggioDepth() * SLIDER_PRECISION));
		changeSpeedSlider.setValue((int) (preset.getArpeggioSpeed() * SLIDER_PRECISION));

		volumeSlider.setValue((int) (preset.getMasterVolume() * SLIDER_PRECISION));
	}

	public void updateVisualizer() {
		var syn = new Synth(preset, rng);
		var sound = syn.createSound(rng);
		soundVisualizer.updateHistogram(sound);
		soundVisualizer.repaint();
	}

	private boolean useSystemFonts() {
		var windows = System.getProperty("os.name", "").toLowerCase().contains("windows");
		var systemFonts = System.getProperty("swing.useSystemFontSettings");
		return windows && Boolean.parseBoolean(systemFonts);
	}

	/**
	 * generates a new wave file of the sound.
	 *
	 * @param sound
	 * @param os
	 * @throws IOException
	 */
	private void write(Sound sound, OutputStream os) throws IOException {
		long length = sound.getPcm().length;
		var format = sound.createAudioFormat(8, 2, 2, false);
		try (var is = new AudioInputStream(new ByteArrayInputStream(sound.getPcm()), format, length)) {
			var type = AudioFileFormat.Type.WAVE;
			if (AudioSystem.isFileTypeSupported(type, is)) {
				AudioSystem.write(is, type, os);
			}
		}
	}
}
