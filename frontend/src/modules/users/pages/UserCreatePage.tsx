import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft } from 'lucide-react';
import { createUserSchema, type CreateUserFormData } from '../schemas/user.schema';
import { useCreateUser } from '../hooks/useUsers';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';
import { apiService } from '@/shared/services/api.service';

export const UserCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const createMutation = useCreateUser();

  const { data: rolesData } = useQuery({
    queryKey: ['roles'],
    queryFn: () => apiService.get<any[]>('/roles'),
  });

  const { register, handleSubmit, formState: { errors } } = useForm<CreateUserFormData>({
    resolver: zodResolver(createUserSchema),
  });

  const onSubmit = (data: CreateUserFormData) => {
    createMutation.mutate(data, {
      onSuccess: () => navigate('/users'),
    });
  };

  const roles = rolesData?.data || [];

  return (
    <div>
      <div className="mb-6">
        <Button variant="ghost" onClick={() => navigate('/users')}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Users
        </Button>
      </div>
      <Card className="max-w-2xl">
        <CardHeader>
          <h2 className="text-xl font-bold text-gray-900">Create User</h2>
          <p className="text-gray-500 text-sm mt-1">Add a new user to the system</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                id="employeeId"
                label="Employee ID"
                error={errors.employeeId?.message}
                {...register('employeeId')}
              />
              <Input
                id="email"
                label="Email"
                type="email"
                error={errors.email?.message}
                {...register('email')}
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <Input
                id="firstName"
                label="First Name"
                error={errors.firstName?.message}
                {...register('firstName')}
              />
              <Input
                id="lastName"
                label="Last Name"
                error={errors.lastName?.message}
                {...register('lastName')}
              />
            </div>
            <Input
              id="mobileNumber"
              label="Mobile Number"
              {...register('mobileNumber')}
            />
            <Input
              id="password"
              label="Password"
              type="password"
              error={errors.password?.message}
              {...register('password')}
            />
            <Select
              id="roleIds"
              label="Roles"
              placeholder="Select roles"
              options={roles.map((r: any) => ({ value: String(r.id), label: r.name }))}
              {...register('roleIds', { setValueAs: (v: string) => v ? [Number(v)] : [] })}
              error={errors.roleIds?.message as string}
            />
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="outline" onClick={() => navigate('/users')}>Cancel</Button>
              <Button type="submit" isLoading={createMutation.isPending}>Create User</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
