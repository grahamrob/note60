package nerdsquad.note60;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by dylan on 2/28/16.
 */
public class Note {
    private FloatBuffer noteVertices;
    private FloatBuffer noteColors;
    private FloatBuffer noteNormals;
    private int noteProgram;
    private int notePositionParam;
    private int noteNormalParam;
    private int noteColorParam;
    private int noteModelParam;
    private int noteModelViewParam;
    private int noteModelViewProjectionParam;
    private int noteLightPosParam;
    private float[] modelNote;
    private String message;
    private static final int COORDS_PER_VERTEX = 3; /*maybe this should only be located in MainActivity class? idk*/

    private static final float[] NOTE_COORDS = new float[] {
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
    };

    private static final float[] NOTE_NORMALS = new float[] {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
    };

    private static final float[] NOTE_COLORS = new float[] {
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
    };

    public Note(String noteMessage, int glProgram, float x, float y, float z){
        message = noteMessage;
        noteProgram = glProgram;

        ByteBuffer bbNoteVertices = ByteBuffer.allocateDirect(NOTE_COORDS.length * 4);
        bbNoteVertices.order(ByteOrder.nativeOrder());
        noteVertices = bbNoteVertices.asFloatBuffer();
        noteVertices.put(NOTE_COORDS);
        noteVertices.position(0);

        ByteBuffer bbNoteColors = ByteBuffer.allocateDirect(NOTE_COLORS.length * 4);
        bbNoteColors.order(ByteOrder.nativeOrder());
        noteColors = bbNoteColors.asFloatBuffer();
        noteColors.put(NOTE_COLORS);
        noteColors.position(0);

        ByteBuffer bbNoteNormals = ByteBuffer.allocateDirect(NOTE_NORMALS.length * 4);
        bbNoteNormals.order(ByteOrder.nativeOrder());
        noteNormals = bbNoteNormals.asFloatBuffer();
        noteNormals.put(NOTE_NORMALS);
        noteNormals.position(0);

        GLES20.glUseProgram(noteProgram);

        notePositionParam = GLES20.glGetAttribLocation(noteProgram, "a_Position");
        noteNormalParam = GLES20.glGetAttribLocation(noteProgram, "a_Normal");
        noteColorParam = GLES20.glGetAttribLocation(noteProgram, "a_Color");

        noteModelParam = GLES20.glGetUniformLocation(noteProgram, "u_Model");
        noteModelViewParam = GLES20.glGetUniformLocation(noteProgram, "u_MVMatrix");
        noteModelViewProjectionParam = GLES20.glGetUniformLocation(noteProgram, "u_MVP");
        noteLightPosParam = GLES20.glGetUniformLocation(noteProgram, "u_LightPos");


        GLES20.glEnableVertexAttribArray(notePositionParam);
        GLES20.glEnableVertexAttribArray(noteNormalParam);
        GLES20.glEnableVertexAttribArray(noteColorParam);

        modelNote = new float[16];
        Matrix.setIdentityM(modelNote, 0);
        Matrix.translateM(modelNote, 0, x, y, z);

        //need to rotate the note to face the origin (still figuring this out)
        //Matrix.rotateM(modelNote, 0, (float) Math.toDegrees(Math.asin(x/z)), 0f, 1.0f, 0f);
        //Matrix.rotateM(modelNote, 0, 90.0f, 0.0f, 1.0f, 0.0f);
    }

    public void drawNote(float[] view, float[] perspective, float[] lightPosInEyeSpace) {
        GLES20.glUseProgram(noteProgram);
        float[] modelView = new float[16];
        float[] modelViewProjection = new float[16];
        Matrix.multiplyMM(modelView, 0, view, 0, modelNote, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);

        GLES20.glUseProgram(noteProgram);

        GLES20.glUniform3fv(noteLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(noteModelParam, 1, false, modelNote, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(noteModelViewParam, 1, false, modelView, 0);

        // Set the position of the note
        GLES20.glVertexAttribPointer(
                notePositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, noteVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(noteModelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the note, again for shading
        GLES20.glVertexAttribPointer(noteNormalParam, 3, GLES20.GL_FLOAT, false, 0, noteNormals);
        GLES20.glVertexAttribPointer(noteColorParam, 4, GLES20.GL_FLOAT, false, 0, noteColors);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

}
