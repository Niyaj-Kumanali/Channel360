import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Plus, Edit, Trash2, ToggleLeft, ToggleRight, Key } from 'lucide-react';
import { useUsers, useDeleteUser, useToggleUserStatus, useResetPassword } from '../hooks/useUsers';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { Badge } from '@/shared/components/ui/Badge';
import { DataTable, type Column } from '@/shared/components/ui/DataTable';
import { Modal } from '@/shared/components/ui/Modal';
import { USER_STATUS } from '@/shared/constants';
import type { UserDto } from '../api/users.api';

export const UserListPage: React.FC = () => {
  const navigate = useNavigate();
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [page, setPage] = useState(0);
  const [deleteModal, setDeleteModal] = useState<{ open: boolean; userId: number | null }>({ open: false, userId: null });

  const { data, isLoading } = useUsers({ search, status: statusFilter, page, size: 10 });
  const deleteMutation = useDeleteUser();
  const toggleMutation = useToggleUserStatus();
  const resetPasswordMutation = useResetPassword();

  const columns: Column<UserDto>[] = [
    { key: 'employeeId', header: 'Employee ID' },
    {
      key: 'name',
      header: 'Name',
      render: (user) => `${user.firstName} ${user.lastName}`,
    },
    { key: 'email', header: 'Email' },
    { key: 'mobileNumber', header: 'Mobile' },
    {
      key: 'status',
      header: 'Status',
      render: (user) => (
        <Badge variant={user.status === 'ACTIVE' ? 'success' : 'danger'}>
          {user.status}
        </Badge>
      ),
    },
    {
      key: 'roles',
      header: 'Roles',
      render: (user) => (
        <div className="flex gap-1 flex-wrap">
          {user.roles?.map((role) => (
            <Badge key={role.id} variant="info">{role.name}</Badge>
          ))}
        </div>
      ),
    },
    {
      key: 'actions',
      header: '',
      render: (user) => (
        <div className="flex items-center justify-end gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate(`/users/${user.id}/edit`)}
          >
            <Edit className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => toggleMutation.mutate({ id: user.id, status: user.status })}
          >
            {user.status === 'ACTIVE' ? <ToggleRight className="h-4 w-4 text-green-600" /> : <ToggleLeft className="h-4 w-4 text-gray-400" />}
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => resetPasswordMutation.mutate(user.id)}
          >
            <Key className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => setDeleteModal({ open: true, userId: user.id })}
          >
            <Trash2 className="h-4 w-4 text-red-500" />
          </Button>
        </div>
      ),
    },
  ];

  const users = data?.content || [];
  const totalPages = data?.totalPages || 0;
  const totalElements = data?.totalElements || 0;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Users</h1>
          <p className="text-gray-500 mt-1">Manage system users</p>
        </div>
        <Link to="/users/new">
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            Add User
          </Button>
        </Link>
      </div>

      <div className="flex gap-4 mb-6">
        <div className="flex-1">
          <Input
            placeholder="Search by name, email, or employee ID..."
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(0); }}
          />
        </div>
        <div className="w-48">
          <Select
            options={[
              { value: '', label: 'All Status' },
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
            ]}
            value={statusFilter}
            onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}
          />
        </div>
      </div>

      <DataTable
        columns={columns}
        data={users}
        isLoading={isLoading}
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        onPageChange={setPage}
      />

      <Modal
        isOpen={deleteModal.open}
        onClose={() => setDeleteModal({ open: false, userId: null })}
        title="Confirm Delete"
        size="sm"
      >
        <p className="text-gray-600 mb-6">Are you sure you want to delete this user? This action cannot be undone.</p>
        <div className="flex justify-end gap-3">
          <Button variant="outline" onClick={() => setDeleteModal({ open: false, userId: null })}>
            Cancel
          </Button>
          <Button
            variant="destructive"
            isLoading={deleteMutation.isPending}
            onClick={() => {
              if (deleteModal.userId) {
                deleteMutation.mutate(deleteModal.userId);
                setDeleteModal({ open: false, userId: null });
              }
            }}
          >
            Delete
          </Button>
        </div>
      </Modal>
    </div>
  );
};
