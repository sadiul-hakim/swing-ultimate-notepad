package notepad;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import java.util.Optional;
import org.json.JSONObject;



public class NotePad extends JFrame implements ActionListener{
    private JMenuItem newItem;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem saveAs;
    private JMenuItem print;
    private JMenuItem exit;
    private JMenuItem cut;
    private JMenuItem copy;
    private JMenuItem paste;
    private JMenuItem selectAll;
    private JMenuItem about;
    private JMenuItem font;
    private JMenuItem theme;
    private JCheckBoxMenuItem lineWrap;
    
    
    private JTextArea mainArea;
    private JScrollPane areaScrollPane;
    private int mainAreaFontSize=12;
    private Font areaFont;
    
    
    private JMenuItem newFileTool;
    private JMenuItem saveTool;
    private JMenuItem printTool;
    private JMenuItem undoTool;
    private JMenuItem redoTool;
    private JMenuItem zoomInTool;
    private JMenuItem zoomOutTool;
    
    private UndoManager undoManager=new UndoManager();
    
    
    
    private static final JFileChooser fileChooser=new JFileChooser();
    private static final  FileNameExtensionFilter extensionFilter=new FileNameExtensionFilter("Only text file (.txt)","txt");
    private File openedFile=null;
    private static Optional<String> defaultTheme=Optional.ofNullable("com.jtattoo.plaf.luna.LunaLookAndFeel");
    
    //initial values
    private static Optional<String> initialFontName=Optional.ofNullable(null);
    private static Optional<Integer> initialFontStyle=Optional.ofNullable(null);
    private static Optional<Integer> initialFontSize=Optional.ofNullable(null);
    private static Optional<Color> initialColor=Optional.ofNullable(null);
    private static Optional<String> initialTheme=Optional.ofNullable(null);
    
    //upcoing data
    Optional<String> selectedName=Optional.ofNullable(null);
    Optional<Integer> selectedStyle=Optional.ofNullable(null);
    Optional<Integer> selectedSize=Optional.ofNullable(null);
    Optional<Color> colorSelected=Optional.ofNullable(null);
    Optional<String> themeSelected=Optional.ofNullable(null);
    
    //constructor
    NotePad(Optional<String> selectedName,Optional<Integer> selectedStyle,Optional<Integer> selectedSize,Optional<Color> colorSelected,Optional<String> themeSelected){
        //setting data
        this.selectedName=selectedName;
        this.selectedStyle=selectedStyle;
        this.selectedSize=selectedSize;
        this.colorSelected=colorSelected;
        this.themeSelected=themeSelected;
                
        //vars
        ImageIcon image=new ImageIcon(ClassLoader.getSystemResource("images/Notepad-icon.png"));
        
               
        if(this.themeSelected.isPresent()){
            defaultTheme=Optional.ofNullable(this.themeSelected.get());
        }

        this.setTitle("Notepad");
        this.setIconImage(image.getImage());
                
        try{
            UIManager.setLookAndFeel(defaultTheme.get());
        }catch(Exception e){
            e.printStackTrace();
        }
                
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800,600);
        this.setLocationRelativeTo(null);
        
        frameMenu();
        mainAreaAction();
        handleToolBar();
        
               
        
    }
    
        
    private void mainAreaAction(){
        String fontName;
        int fontStyle;
        int fontSize;
        Color fontColor;
        //vars
        mainArea=new JTextArea();
        areaScrollPane =new JScrollPane(mainArea);
        areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainArea.setWrapStyleWord(true);
        mainArea.setMargin(new Insets(5, 5, 0, 0));
        //take docs
        mainArea.getDocument().addUndoableEditListener(e->undoManager.addEdit(e.getEdit()));
        
        if(selectedName.isPresent()){
            fontName=selectedName.get();
        }else{
            fontName="Arial";
        }
        
        if(selectedStyle.isPresent()){
            fontStyle=selectedStyle.get();
            
        }else{
            fontStyle=Font.PLAIN;
        }
        
        if(selectedSize.isPresent()){
            fontSize=selectedSize.get();
            
        }else{
            fontSize=12;
        }
        
        if(colorSelected.isPresent()){
            fontColor=colorSelected.get();
            
        }else{
            fontColor=Color.BLACK;
        }
        
        areaFont=new Font(fontName,fontStyle,fontSize);
        mainArea.setFont(areaFont);
        mainArea.setForeground(fontColor);
        
        
        this.add(areaScrollPane);
        
    }
    private void frameMenu(){
        JMenuBar menuBar=new JMenuBar();
        
        JMenu file=new JMenu("File");
        JMenu edit=new JMenu("Edit");
        JMenu view=new JMenu("View");
        JMenu format=new JMenu("Format");
        JMenu help=new JMenu("Help");
        
        //file menu item
        newItem=new JMenuItem("New");
        open=new JMenuItem("Open");
        save=new JMenuItem("Save");
        saveAs=new JMenuItem("Save As");
        print=new JMenuItem("Print");
        exit=new JMenuItem("Exit");
        file.add(newItem);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.add(print);
        file.add(exit);
        
        //edit menu items
        cut=new JMenuItem("Cut");
        copy=new JMenuItem("Copy");
        paste=new JMenuItem("Paste");
        selectAll=new JMenuItem("SelectAll");
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);
        
        //help menu item
        about=new JMenuItem("About");
        help.add(about);
        
        //format
        font=new JMenuItem("Font");
        theme=new JMenuItem("Theme");
        lineWrap=new JCheckBoxMenuItem("Line Wrap");
        
        font.addActionListener(this);
        theme.addActionListener(this);
        lineWrap.addActionListener(this);
        
        format.add(font);
        format.add(theme);
        format.add(lineWrap);
        
        
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(view);
        menuBar.add(format);
        menuBar.add(help);
        
        
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_DOWN_MASK));
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.CTRL_DOWN_MASK));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_DOWN_MASK));
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.ALT_DOWN_MASK));
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,KeyEvent.CTRL_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,KeyEvent.CTRL_DOWN_MASK));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,KeyEvent.CTRL_DOWN_MASK));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_DOWN_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,KeyEvent.CTRL_DOWN_MASK));
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_DOWN_MASK));
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,KeyEvent.CTRL_DOWN_MASK));
        
        
        
        about.addActionListener(this);
        newItem.addActionListener(this);
        open.addActionListener(this);
        save.addActionListener(this);
        saveAs.addActionListener(this);
        print.addActionListener(this);
        exit.addActionListener(this);
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectAll.addActionListener(this);
        
        this.setJMenuBar(menuBar);
    }
    private void handleToolBar(){
        
        JToolBar toolbar=new JToolBar(JToolBar.HORIZONTAL);
        this.add(toolbar,BorderLayout.NORTH);
        toolbar.setFloatable(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        
        ImageIcon newFile=new ImageIcon(ClassLoader.getSystemResource("images/Files-New-File-icon.png"));                
        ImageIcon saveIcon=new ImageIcon(ClassLoader.getSystemResource("images/Actions-document-save-icon.png"));           
        ImageIcon printIcon=new ImageIcon(ClassLoader.getSystemResource("images/print-icon.png"));
        
        ImageIcon undoIcon=new ImageIcon(ClassLoader.getSystemResource("images/Arrows-Undo-icon.png"));
        ImageIcon redoIcon=new ImageIcon(ClassLoader.getSystemResource("images/Arrows-Redo-icon.png"));
        ImageIcon zoomInIcon=new ImageIcon(ClassLoader.getSystemResource("images/Zoom-In-icon.png"));
        ImageIcon zoomOutIcon=new ImageIcon(ClassLoader.getSystemResource("images/Misc-Zoom-Out-icon.png"));
        
        
        newFileTool=new JMenuItem(newFile);
        saveTool=new JMenuItem(saveIcon);
        printTool=new JMenuItem(printIcon);
        undoTool=new JMenuItem(undoIcon);
        redoTool=new JMenuItem(redoIcon);
        zoomInTool=new JMenuItem(zoomInIcon);
        zoomOutTool=new JMenuItem(zoomOutIcon);
        
        newFileTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        undoTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        redoTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        zoomInTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        zoomOutTool.setCursor(new Cursor(Cursor.HAND_CURSOR));

        
        newFileTool.addActionListener(this);
        saveTool.addActionListener(this);
        printTool.addActionListener(this);
        undoTool.addActionListener(this);
        redoTool.addActionListener(this);
        zoomInTool.addActionListener(this);
        zoomOutTool.addActionListener(this);
        
        newFileTool.setToolTipText("New File");
        printTool.setToolTipText("Print");
        saveTool.setToolTipText("Save");
        undoTool.setToolTipText("Undo");
        redoTool.setToolTipText("Redo");
        zoomInTool.setToolTipText("Zoom In");
        zoomOutTool.setToolTipText("Zoom Out");



        
        toolbar.add(newFileTool);
        toolbar.add(saveTool);
        toolbar.add(printTool);
        toolbar.add(undoTool);
        toolbar.add(redoTool);
        toolbar.add(zoomInTool);
        toolbar.add(zoomOutTool);


        
        
    }
    public static void main(String[] args) {
        
        try{
           Path path=Path.of(ClassLoader.getSystemResource("notepad/settings.json").toURI());
           String text=Files.readString(path);
           JSONObject json=new JSONObject(text);
           
           String name=json.get("FontName").toString();
           int style=(int) json.get("FontStyle");
           int size=(int)json.get("FontSize");
           String color=json.get("FontColor").toString();
           String theme=json.get("Theme").toString();
           
                       
           initialFontName=Optional.ofNullable(name);
           initialFontStyle=Optional.ofNullable(style);
           initialFontSize=Optional.ofNullable(size);
           initialColor=Optional.ofNullable(Color.getColor(color, Color.black));
           initialTheme=Optional.ofNullable(theme);
           
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        NotePad frame=new NotePad(initialFontName,initialFontStyle,initialFontSize,initialColor,initialTheme);
        frame.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
                
       if(e.getSource()==about){
           About about=new About();
           about.setVisible(true);
       }else if(e.getSource()==exit){
           System.exit(0);
       }else if(e.getSource()==newItem || e.getSource()==newFileTool){
           mainArea.setText(null);
       }else if(e.getSource()==open){
           fileChooser.setAcceptAllFileFilterUsed(false);
           fileChooser.addChoosableFileFilter(extensionFilter);
           
           int action=fileChooser.showOpenDialog(null);
           
           if(action!=JFileChooser.APPROVE_OPTION){
               return;
           }else{
               openedFile=fileChooser.getSelectedFile().getAbsoluteFile();
              String name=fileChooser.getName(openedFile);
              
               this.setTitle(name+" - Notepad");
               try{
                   
                   BufferedReader reader=new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                   mainArea.read(reader,null);
               }catch(Exception ex){
                   
               }
               
           }
                      
       }else if(e.getSource()==saveAs){
           
           
           fileChooser.setAcceptAllFileFilterUsed(false);
           fileChooser.addChoosableFileFilter(extensionFilter);
           
           int action=fileChooser.showSaveDialog(null);
           
           if(action!=JFileChooser.APPROVE_OPTION){
               return;
           }else{
               String filename=fileChooser.getSelectedFile().getAbsolutePath().toString();
               if(!filename.endsWith(".txt")){
                   filename=filename+".txt";
                   System.out.println(filename);
               }
               
               try{
                   
                   BufferedWriter writter=new BufferedWriter(new FileWriter(filename));
                   mainArea.write(writter);
               }catch(Exception ex){
                   ex.printStackTrace();
               }                   
           }
                      
       }else if(e.getSource()==print || e.getSource()==printTool){
           try{
               mainArea.print();
           }catch(Exception ex){
               ex.printStackTrace();
           }
       }else if(e.getSource()==cut){
           mainArea.cut();
       }else if(e.getSource()==copy){
           mainArea.copy();
       }else if(e.getSource()==paste){
           mainArea.paste();
       }else if(e.getSource()==selectAll){
           mainArea.selectAll();
       }else if(e.getSource()==save || e.getSource()==saveTool){
           //contains bug
           String text=mainArea.getText();
           if(openedFile==null){
                    fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(extensionFilter);

                int action=fileChooser.showSaveDialog(null);

                if(action!=JFileChooser.APPROVE_OPTION){
                    return;
                }else{
                    String filename=fileChooser.getSelectedFile().getAbsolutePath().toString();
                    if(!filename.endsWith(".txt")){
                        filename=filename+".txt";
                        System.out.println(filename);
                    }

                    try{

                        BufferedWriter writter=new BufferedWriter(new FileWriter(filename));
                        mainArea.write(writter);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }                   
                }
           }
           else{
              Path path=Path.of(openedFile.toString());
              try{
                  Files.writeString(path, text);
              }catch(Exception ex){
                  
              }
           }
       }else if(e.getSource()==undoTool){
               System.out.println("Hakim");
               undoManager.undo();
       }else if(e.getSource()==redoTool){
               undoManager.redo();
       }else if(e.getSource()==zoomInTool){
           if(mainAreaFontSize<=36){
               mainAreaFontSize+=4;
               areaFont=new Font("Arial",Font.PLAIN,mainAreaFontSize);
               mainArea.setFont(areaFont);
           }
       }else if(e.getSource()==zoomOutTool){
           if(mainAreaFontSize>=12){
               mainAreaFontSize-=4;
               areaFont=new Font("Arial",Font.PLAIN,mainAreaFontSize);
               mainArea.setFont(areaFont);
           }
       }else if(e.getSource()==font){
           
          FontPallet frame=new FontPallet(selectedName,selectedStyle,selectedSize,colorSelected,defaultTheme);
          frame.setVisible(true);
          this.dispose();
           
       }else if(e.getSource()==theme){
          ThemePallet frame=new ThemePallet(selectedName,selectedStyle,selectedSize,colorSelected,defaultTheme);
        frame.setVisible(true);
        
        this.dispose();
           
       }else if(e.getSource()==lineWrap){
           mainArea.setLineWrap(true);
       }
    
    
    }
    
}
