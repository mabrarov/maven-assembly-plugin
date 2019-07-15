package org.apache.maven.plugins.assembly.archive.task;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;
import org.apache.maven.plugins.assembly.archive.ArchiveCreationException;
import org.apache.maven.plugins.assembly.testutils.TestFileManager;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.Owner;
import org.easymock.classextension.EasyMockSupport;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

public class AddDirectoryTaskTest
    extends TestCase
{

    private EasyMockSupport mockManager;

    private TestFileManager fileManager;

    private Archiver archiver;


    public void setUp()
    {
        fileManager = new TestFileManager( "ArchiveAssemblyUtils.test.", "" );

        mockManager = new EasyMockSupport();

        archiver = mockManager.createMock( Archiver.class );
    }

    public void tearDown()
        throws IOException
    {
        fileManager.cleanUp();
    }

    public void testAddDirectory_ShouldNotAddDirectoryIfNonExistent()
        throws ArchiveCreationException
    {
        File dir = new File( System.getProperty( "java.io.tmpdir" ), "non-existent." + System.currentTimeMillis() );

        configureModeExpectations( -1, -1, -1, -1, false );
        configureOwnerExpectations( null, null, null, null, false );

        mockManager.replayAll();

        AddDirectoryTask task = new AddDirectoryTask( dir );

        task.execute( archiver );

        mockManager.verifyAll();
    }

    public void testAddDirectory_ShouldAddDirectory()
        throws ArchiveCreationException
    {
        File dir = fileManager.createTempDir();

        try
        {
            archiver.addFileSet( (FileSet) anyObject() );
        }
        catch ( ArchiverException e )
        {
            fail( "Should never happen." );
        }

        configureModeExpectations( -1, -1, -1, -1, false );
        configureOwnerExpectations( null, null, null, null, false );

        mockManager.replayAll();

        AddDirectoryTask task = new AddDirectoryTask( dir );

        task.setOutputDirectory( "dir" );

        task.execute( archiver );

        mockManager.verifyAll();
    }

    public void testAddDirectory_ShouldAddDirectoryWithDirMode()
        throws ArchiveCreationException
    {
        File dir = fileManager.createTempDir();

        try
        {
            archiver.addFileSet( (FileSet) anyObject() );
        }
        catch ( ArchiverException e )
        {
            fail( "Should never happen." );
        }

        int dirMode = Integer.parseInt( "777", 8 );
        int fileMode = Integer.parseInt( "777", 8 );

        configureModeExpectations( -1, -1, dirMode, fileMode, true );
        // TODO: add test for validation of owner changing
        configureOwnerExpectations( null, null, null, null, false );

        mockManager.replayAll();

        AddDirectoryTask task = new AddDirectoryTask( dir );

        task.setDirectoryMode( dirMode );
        task.setFileMode( fileMode );
        task.setOutputDirectory( "dir" );

        task.execute( archiver );

        mockManager.verifyAll();
    }

    public void testAddDirectory_ShouldAddDirectoryWithIncludesAndExcludes()
        throws ArchiveCreationException
    {
        File dir = fileManager.createTempDir();

        try
        {
            archiver.addFileSet( (FileSet) anyObject() );
        }
        catch ( ArchiverException e )
        {
            fail( "Should never happen." );
        }

        configureModeExpectations( -1, -1, -1, -1, false );
        configureOwnerExpectations( null, null, null, null, false );

        mockManager.replayAll();

        AddDirectoryTask task = new AddDirectoryTask( dir );

        task.setIncludes( Collections.singletonList( "**/*.txt" ) );
        task.setExcludes( Collections.singletonList( "**/README.txt" ) );
        task.setOutputDirectory( "dir" );

        task.execute( archiver );

        mockManager.verifyAll();
    }

    private void configureModeExpectations( int defaultDirMode, int defaultFileMode, int dirMode, int fileMode,
                                            boolean expectTwoSets )
    {
        expect( archiver.getOverrideDirectoryMode() ).andReturn( defaultDirMode );
        expect( archiver.getOverrideFileMode() ).andReturn( defaultFileMode );

        if ( expectTwoSets )
        {
            if ( dirMode > -1 )
            {
                archiver.setDirectoryMode( dirMode );
            }

            if ( fileMode > -1 )
            {
                archiver.setFileMode( fileMode );
            }
        }

        if ( dirMode > -1 )
        {
            archiver.setDirectoryMode( defaultDirMode );
        }

        if ( fileMode > -1 )
        {
            archiver.setFileMode( defaultFileMode );
        }
    }

    private void configureOwnerExpectations( Owner defaultDirOwner, Owner defaultFileOwner, Owner dirOwner,
                                             Owner fileOwner, boolean expectTwoSets )
    {
        // TODO: fix validation of expected owner:
        // compare not just references but properties of returned Owner instance, make verification null safe
        expect( archiver.getOverrideDirectoryOwner() ).andReturn( defaultDirOwner );
        // TODO: fix validation of expected owner:
        // compare not just references but properties of returned Owner instance, make verification null safe
        expect( archiver.getOverrideFileOwner() ).andReturn( defaultFileOwner );

        if ( expectTwoSets )
        {
            if ( dirOwner != null )
            {
                archiver.setDirectoryOwner( dirOwner );
            }

            if ( fileOwner != null )
            {
                archiver.setFileOwner( fileOwner );
            }
        }

        if ( dirOwner != null )
        {
            archiver.setDirectoryOwner( defaultDirOwner );
        }

        if ( fileOwner != null )
        {
            archiver.setFileOwner( defaultFileOwner );
        }
    }

}
