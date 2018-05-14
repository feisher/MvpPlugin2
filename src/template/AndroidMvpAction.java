package template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jf.util.StringUtils;

import java.io.*;
/**
* explin:
* auther:Administrator & feisher(458079442@qq.com)
* create by 2018/5/14/014 12:48
*/
public class AndroidMvpAction extends AnAction {
    Project project;
    VirtualFile selectGroup;
    private String laytouName;
    private String mvpPath ="";

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        String className = Messages.showInputDialog(project, "请输入类名称(Please enter a class name)", "NewMvpGroup", Messages.getQuestionIcon());
        selectGroup = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (className == null || className.equals("")) {
            System.out.print("没有输入类名（no class name）");
            return;
        }
        if (className.equals("mvp") || className.equals("MVP") || className.equals("Mvp")) {
            createMvpBase();
        } else {
            createClassMvp(className);
        }
        project.getBaseDir().refresh(false,true);
    }

    /**
     * 创建MVP的Base文件夹
     */
    private void createMvpBase() {
        String path = selectGroup.getPath() + "/mvp";
        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");

        String presenter = readFile("BasePresenter.txt").replace("&package&", packageName);
        String presenterImpl = readFile("BasePresenterImpl.txt").replace("&package&", packageName);
        String view = readFile("BaseView.txt").replace("&package&", packageName);
        String activity = readFile("MvpBaseActivity.txt").replace("&package&", packageName);
        String fragment = readFile("MvpBaseFragment.txt").replace("&package&", packageName);

        writetoFile(presenter, path, "BasePresenter.java");
        writetoFile(presenterImpl, path, "BasePresenterImpl.java");
        writetoFile(view, path, "BaseView.java");
        writetoFile(activity, path, "MvpBaseActivity.java");
        writetoFile(fragment, path, "MvpBaseFragment.java");

    }

    /**
     * 创建MVP架构
     */
    private void createClassMvp(String className) {
        boolean isFragment = className.endsWith("Fragment") || className.endsWith("fragment");
        if (className.endsWith("Fragment") || className.endsWith("fragment") || className.endsWith("Activity") || className.endsWith("activity")) {
            className = className.substring(0,className.length() - 8);
        }

        String path = selectGroup.getPath() + "/" + className.toLowerCase();
        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");

        String mvpPath = getMvpPath(project.getBaseDir());
        mvpPath=mvpPath.substring(mvpPath.indexOf("java") + 5, mvpPath.length()).replace("/", ".").replace("\\",".");

        className = className.substring(0, 1).toUpperCase() + className.substring(1);

        System.out.print(mvpPath+"---"+className+"----"+packageName);

        String contract = readFile("Contract.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Contract&", className + "Contract");
        String presenter = readFile("Presenter.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");

        laytouName = "";
        if (isFragment) {
            String fragment = readFile("Fragment.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Fragment&", className + "Fragment").replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
            writetoFile(fragment, path, className + "Fragment.java");
            laytouName = "fragment_"+className.substring(0,className.length() - 8).toLowerCase();
        } else {
            String activity = readFile("Activity.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Activity&", className + "Activity").replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
            writetoFile(activity, path, className + "Activity.java");
            laytouName = "activity_"+className.substring(0,className.length() - 8).toLowerCase();
        }
        writetoFile(contract, path, className + "Contract.java");
        writetoFile(presenter, path, className + "Presenter.java");
    }

    /**
     * 获取mvp初始文件路径(兼容使用公共库情况)
     * @return
     */
    private String getMvpPath(VirtualFile file) {
        for (VirtualFile virtualFile : file.getChildren()) {
            String name = virtualFile.getPath();
            if (virtualFile.isDirectory()) {
                if (name.endsWith("mvp")) {
                    if (mvpPath == "") {
                           mvpPath = virtualFile.getPath();
                    }
                    break;
                }else {
                    if (mvpPath == "") {
                        getMvpPath(virtualFile);
                    }else {
                        break;
                    }
                }
            }else {
                continue;
            }
        }
        return mvpPath;
    }


    private String readFile(String filename) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream("code/" + filename);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        return content;
    }

    private void writetoFile(String content, String filepath, String filename) {
        try {
            File floder = new File(filepath);
            // if file doesnt exists, then create it
            if (!floder.exists()) {
                floder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }

}
